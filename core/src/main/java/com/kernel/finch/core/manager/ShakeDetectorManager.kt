package com.kernel.finch.core.manager

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.*

private const val SENSITIVITY_LIGHT = 11
private const val SENSITIVITY_MEDIUM = 13
private const val SENSITIVITY_HARD = 15
private const val DEFAULT_ACCELERATION_THRESHOLD = SENSITIVITY_MEDIUM

/**
 * Detects phone shaking. If more than 75% of the samples taken in the past 0.5s are
 * accelerating, the device is a) shaking, or b) free falling 1.84m (h =
 * 1/2*g*t^2*3/4)
 */
internal class ShakeDetectorManager(private val listener: () -> Unit) : SensorEventListener {
    /**
     * When the magnitude of total acceleration exceeds this
     * value, the phone is accelerating.
     */
    private var accelerationThreshold = DEFAULT_ACCELERATION_THRESHOLD

    private val queue = SampleQueue()
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    /**
     * Starts listening for shakes on devices with appropriate hardware.
     *
     * @return true if the device supports shake detection.
     */
    fun start(sensorManager: SensorManager): Boolean {
        // Already started?
        if (accelerometer != null) {
            return true
        }
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // If this phone has an accelerometer, listen to it.
        if (accelerometer != null) {
            this.sensorManager = sensorManager
            sensorManager.registerListener(
                this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        return accelerometer != null
    }

    /**
     * Stops listening.  Safe to call when already stopped.  Ignored on devices
     * without appropriate hardware.
     */
    fun stop() {
        accelerometer?.let {
            queue.clear()
            sensorManager?.unregisterListener(this, it)
            sensorManager = null
            accelerometer = null
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val accelerating = isAccelerating(event)
        val timestamp = event.timestamp
        queue.add(timestamp, accelerating)
        if (queue.isShaking) {
            queue.clear()
            listener()
        }
    }

    /** Returns true if the device is currently accelerating.  */
    private fun isAccelerating(event: SensorEvent): Boolean {
        val ax = event.values[0]
        val ay = event.values[1]
        val az = event.values[2]

        // Instead of comparing magnitude to ACCELERATION_THRESHOLD,
        // compare their squares. This is equivalent and doesn't need the
        // actual magnitude, which would be computed using (expensive) Math.sqrt().
        val magnitudeSquared = (ax * ax + ay * ay + az * az).toDouble()
        return magnitudeSquared > accelerationThreshold * accelerationThreshold
    }

    /** Sets the acceleration threshold sensitivity.  */
    fun setSensitivity(accelerationThreshold: Int) {
        this.accelerationThreshold = accelerationThreshold
    }

    /** Queue of samples. Keeps a running average.  */
    internal class SampleQueue {
        private val pool = SamplePool()
        private var oldest: Sample? = null
        private var newest: Sample? = null
        private var sampleCount = 0
        private var acceleratingCount = 0

        /**
         * Adds a sample.
         *
         * @param timestamp    in nanoseconds of sample
         * @param accelerating true if > [.accelerationThreshold].
         */
        fun add(timestamp: Long, accelerating: Boolean) {
            // Purge samples that proceed window.
            purge(timestamp - MAX_WINDOW_SIZE)

            // Add the sample to the queue.
            val added = pool.acquire()
            added.timestamp = timestamp
            added.accelerating = accelerating
            added.next = null
            newest?.let {
                it.next = added
            }
            newest = added
            if (oldest == null) {
                oldest = added
            }

            // Update running average.
            sampleCount++
            if (accelerating) {
                acceleratingCount++
            }
        }

        /** Removes all samples from this queue.  */
        fun clear() {
            while (oldest != null) {
                val removed = oldest ?: continue
                oldest = removed.next
                pool.release(removed)
            }
            newest = null
            sampleCount = 0
            acceleratingCount = 0
        }

        /** Purges samples with timestamps older than cutoff.  */
        private fun purge(cutoff: Long) {
            while (sampleCount >= MIN_QUEUE_SIZE && oldest != null && cutoff - oldest!!.timestamp > 0) {
                // Remove sample.
                val removed = oldest ?: continue
                if (removed.accelerating) {
                    acceleratingCount--
                }
                sampleCount--
                oldest = removed.next
                if (oldest == null) {
                    newest = null
                }
                pool.release(removed)
            }
        }

        /** Copies the samples into a list, with the oldest entry at index 0.  */
        fun asList(): List<Sample> {
            val list: MutableList<Sample> = ArrayList()
            var s = oldest
            while (s != null) {
                list.add(s)
                s = s.next
            }
            return list
        }

        /**
         * Returns true if we have enough samples and more than 3/4 of those samples
         * are accelerating.
         */
        val isShaking: Boolean
            get() = newest != null && oldest != null && newest!!.timestamp - oldest!!.timestamp >= MIN_WINDOW_SIZE && acceleratingCount >= (sampleCount shr 1) + (sampleCount shr 2)

        companion object {
            /** Window size in ns. Used to compute the average.  */
            private const val MAX_WINDOW_SIZE: Long = 500000000 // 0.5s
            private const val MIN_WINDOW_SIZE = MAX_WINDOW_SIZE shr 1 // 0.25s

            /**
             * Ensure the queue size never falls below this size, even if the device
             * fails to deliver this many events during the time window. The LG Ally
             * is one such device.
             */
            private const val MIN_QUEUE_SIZE = 4
        }
    }

    /** An accelerometer sample.  */
    internal class Sample {
        /** Time sample was taken.  */
        var timestamp: Long = 0

        /** If acceleration > [.accelerationThreshold].  */
        var accelerating = false

        /** Next sample in the queue or pool.  */
        var next: Sample? = null
    }

    /** Pools samples. Avoids garbage collection.  */
    internal class SamplePool {
        private var head: Sample? = null

        /** Acquires a sample from the pool.  */
        fun acquire(): Sample {
            var acquired = head
            if (acquired == null) {
                acquired = Sample()
            } else {
                // Remove instance from pool.
                head = acquired.next
            }
            return acquired
        }

        /** Returns a sample to the pool.  */
        fun release(sample: Sample) {
            sample.next = head
            head = sample
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
