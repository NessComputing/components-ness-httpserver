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
package com.nesscomputing.httpserver.selftest;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

public class SelftestModule extends AbstractModule
{
    @Override
    public void configure()
    {
        bind (SelftestResource.class);
    }

    public static LinkedBindingBuilder<Selftest> registerSelftest(final Binder binder)
    {
        return Multibinder.newSetBinder(binder, Selftest.class).addBinding();
    }

    public static AnnotatedBindingBuilder<DefaultSelftest> registerDefaultSelftest(final Binder binder)
    {
        return binder.bind(DefaultSelftest.class);
    }
}

