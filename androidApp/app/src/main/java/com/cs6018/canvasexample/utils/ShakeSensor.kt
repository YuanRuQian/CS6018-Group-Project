// this file is adapt from: https://github.com/square/seismic/blob/master/library/src/main/java/com/squareup/seismic/ShakeDetector.java

package com.cs6018.canvasexample.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

/**
 * Detects phone shaking. If more than 75% of the samples taken in the past 0.5s are
 * accelerating, the device is a) shaking, or b) free falling 1.84m (h =
 * 1/2*g*t^2*3/4).
 *
 * @author Bob Lee (bob@squareup.com)
 * @author Eric Burke (eric@squareup.com)
 */
class ShakeDetector(private val listener: Listener) :
    SensorEventListener {
    /**
     * When the magnitude of total acceleration exceeds this
     * value, the phone is accelerating.
     */

    /** Listens for shakes.  */
    interface Listener {
        fun hearLightShake()
        fun hearHardShake()
    }

    private val queue = SampleQueue()
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    /**
     * Starts listening for shakes on devices with appropriate hardware.
     * Allowing to set the sensor delay, available values are:
     * SENSOR_DELAY_FASTEST, SENSOR_DELAY_GAME, SENSOR_DELAY_UI, SENSOR_DELAY_NORMAL.
     * @see [SensorManager](https://developer.android.com/reference/android/hardware/SensorManager)
     *
     *
     * @return true if the device supports shake detection.
     */
    /**
     * Starts listening for shakes on devices with appropriate hardware.
     *
     * @return true if the device supports shake detection.
     */
    @JvmOverloads
    fun start(
        sensorManager: SensorManager,
        sensorDelay: Int
    ): Boolean {
        // Already started?
        if (accelerometer != null) {
            return true
        }
        accelerometer = sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )

        // If this phone has an accelerometer, listen to it.
        if (accelerometer != null) {
            this.sensorManager = sensorManager
            sensorManager.registerListener(this, accelerometer, sensorDelay)
        }
        return accelerometer != null
    }

    /**
     * Stops listening.  Safe to call when already stopped.  Ignored on devices
     * without appropriate hardware.
     */
    fun stop() {
        if (accelerometer != null) {
            queue.clear()
            sensorManager!!.unregisterListener(this, accelerometer)
            sensorManager = null
            accelerometer = null
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val isLightAccelerating = isAccelerating(event, SensitivityLevel.LIGHT.value)
        val isHardAccelerating = isAccelerating(event, SensitivityLevel.HARD.value)
        val timestamp = event.timestamp
        Log.d(
            "ShakeDetector",
            "onSensorChanged: isLightAccelerating = $isLightAccelerating | isHardAccelerating = $isHardAccelerating"
        )
        queue.add(timestamp, isLightAccelerating, isHardAccelerating)

        if (queue.isHardShaking) {
            queue.clear()
            listener.hearHardShake()
        } else if (queue.isLightShaking) {
            queue.clear()
            listener.hearLightShake()
        }
    }

    /** Returns true if the device is currently accelerating.  */
    private fun isAccelerating(event: SensorEvent, accelerationThreshold: Int): Boolean {
        val ax = event.values[0]
        val ay = event.values[1]
        val az = event.values[2]

        Log.d("ShakeDetector", "isAccelerating: ax = $ax, ay = $ay, az = $az")

        // Instead of comparing magnitude to ACCELERATION_THRESHOLD,
        // compare their squares. This is equivalent and doesn't need the
        // actual magnitude, which would be computed using (expensive) Math.sqrt().
        val magnitudeSquared = (ax * ax + ay * ay + az * az).toDouble()
        Log.d(
            "ShakeDetector",
            "isAccelerating: magnitudeSquared = $magnitudeSquared, accelerationThreshold^2 = ${accelerationThreshold * accelerationThreshold}"
        )
        return magnitudeSquared > accelerationThreshold * accelerationThreshold
    }

    /** Queue of samples. Keeps a running average.  */
    internal class SampleQueue {
        private val pool = SamplePool()
        private var oldest: Sample? = null
        private var newest: Sample? = null
        private var sampleCount = 0
        private var lightAcceleratingCount = 0
        private var hardAcceleratingCount = 0

        /**
         * Adds a sample.
         *
         * @param timestamp    in nanoseconds of sample
         * @param isLightAccelerating true if > [.accelerationThreshold].
         * @param isHardAccelerating true if > [.accelerationThreshold].
         */
        fun add(timestamp: Long, isLightAccelerating: Boolean, isHardAccelerating: Boolean) {
            // Purge samples that proceed window.
            purge(timestamp - MAX_WINDOW_SIZE)

            // Add the sample to the queue.
            val added = pool.acquire()
            added.timestamp = timestamp
            added.isLightAccelerating = isLightAccelerating
            added.isHardAccelerating = isHardAccelerating
            added.next = null
            if (newest != null) {
                newest!!.next = added
            }
            newest = added
            if (oldest == null) {
                oldest = added
            }

            // Update running average.
            sampleCount++
            if (isLightAccelerating) {
                lightAcceleratingCount++
            }

            if (isHardAccelerating) {
                hardAcceleratingCount++
            }
        }

        /** Removes all samples from this queue.  */
        fun clear() {
            while (oldest != null) {
                val removed: Sample = oldest as Sample
                oldest = removed.next
                pool.release(removed)
            }
            newest = null
            sampleCount = 0
            lightAcceleratingCount = 0
            hardAcceleratingCount = 0
        }

        /** Purges samples with timestamps older than cutoff.  */
        fun purge(cutoff: Long) {
            while (sampleCount >= MIN_QUEUE_SIZE && oldest != null && cutoff - oldest!!.timestamp > 0) {
                // Remove sample.
                val removed: Sample = oldest as Sample
                if (removed.isLightAccelerating) {
                    lightAcceleratingCount--
                }
                if (removed.isHardAccelerating) {
                    hardAcceleratingCount--
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


        val isLightShaking: Boolean
            /**
             * Returns true if we have enough samples and more than 3/4 of those samples
             * are light accelerating.
             */
            get() = newest != null && oldest != null && newest!!.timestamp - oldest!!.timestamp >= MIN_WINDOW_SIZE && lightAcceleratingCount >= (sampleCount shr 1) + (sampleCount shr 2)

        val isHardShaking: Boolean
            /**
             * Returns true if we have enough samples and more than 3/4 of those samples
             * are hard accelerating.
             */
            get() = newest != null && oldest != null && newest!!.timestamp - oldest!!.timestamp >= MIN_WINDOW_SIZE && hardAcceleratingCount >= (sampleCount shr 1) + (sampleCount shr 2)


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
        var isLightAccelerating = false
        var isHardAccelerating = false

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
    enum class SensitivityLevel(val value: Int) {
        LIGHT(11),
        HARD(12)
    }
}