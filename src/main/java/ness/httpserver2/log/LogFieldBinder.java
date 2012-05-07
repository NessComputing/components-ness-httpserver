package ness.httpserver2.log;

import ness.httpserver2.log.LogFields.LogField;

import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;


public final class LogFieldBinder
{
    private LogFieldBinder()
    {
    }

    public static LinkedBindingBuilder<LogField> bindField(final Binder binder, final String fieldName)
    {
        final MapBinder<String, LogField> fields = MapBinder.newMapBinder(binder, String.class, LogField.class);
        return fields.addBinding(fieldName);
    }
}
