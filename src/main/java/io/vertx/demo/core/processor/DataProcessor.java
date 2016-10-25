package io.vertx.demo.core.processor;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import java.time.Instant;

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
            final String busProcessedAdress = "median_temperature";
            EventBus eb = vertx.eventBus();

            //When new message on bus
            eb.<JsonObject> consumer(busAddress, message -> {
                if (isNormal(Double.parseDouble( message.body().getString("value")), medianTempValue)) {
                    computeAverage(Double.parseDouble(message.body().getString("value")));

                    if ((rawTempMsgCount % 3) == 0) {
                        JsonObject data = new JsonObject();
                        data.put("timestamp",Instant.now().getEpochSecond())
                                .put("value", medianTempValue);
                        eb.publish(busProcessedAdress, data);

                        System.out.println("Published Average : "+ medianTempValue);
                    }
                }
            });
        }
}