package io.vertx.demo.core.processor;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import io.vertx.demo.util.Runner;

/**
 * Created by jibou on 24/10/16.
 */
public class DataProcessor extends AbstractVerticle {

        // Convenience method so you can run it in your IDE
        public static void main(String[] args) { Runner.runClusteredExample(io.vertx.demo.core.processor.DataProcessor.class);}

         private int rawTempMsgCount = 0;
         private double medianTempValue = 0;


    // Compute the median value with each incoming value
    private void computeAverage(double currentValue) {

            if (rawTempMsgCount != 0) {
                this.medianTempValue = (this.medianTempValue+currentValue)/2; }
            else this.medianTempValue = currentValue;
            this.rawTempMsgCount++;
    }

    // Exclude abnormal values
    private boolean isNormal(double value, double median) {
        if (rawTempMsgCount == 0) return true;
        if ((value > median*1.15) || (value < median*0.85)) return false;
        else return true;
    }

        @Override
        public void start() throws Exception {

            // Bus settings and instance
            final String busAddress = "raw_temperature";
            final String busProcessedAddress = "median_temperature";
            Runtime.getRuntime().exec("python /demo/processor.py");
            EventBus eb = vertx.eventBus();

            //When new message on bus
            eb.<JsonObject> consumer(busAddress, message -> {
                System.out.println("new message");
                System.out.println( message.body().getString("id"));
                Double value = message.body().getDouble("value");
                if (isNormal( value, medianTempValue)) {
                    computeAverage(value);

                      if ((rawTempMsgCount % 4) == 0) {
                        JsonObject data = new JsonObject();
                        data.put("timestamp", System.currentTimeMillis())
                                .put("value", medianTempValue);
                        eb.publish(busProcessedAddress, data);

                    }
                }
            });
        }
}
