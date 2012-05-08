/**
 * Copyright (C) 2012 Ness Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nesscomputing.httpserver.log;


import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import com.nesscomputing.httpserver.log.LogFields.LogField;


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
