
package net.challenge.app.weatherapp.listener;

import net.challenge.app.weatherapp.data.Channel;

public interface WeatherServiceListener {
    void serviceSuccess(Channel channel);
    void serviceFailure(Exception exception);
}
