package com.nesscomputing.httpserver.selftest;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

public class SelftestModule extends AbstractModule
{
    @Override
    public void configure()
    {
        bind (SelfTestResource.class);
    }

    public static LinkedBindingBuilder<SelfTest> registerSelfTest(final Binder binder)
    {
        return Multibinder.newSetBinder(binder, SelfTest.class).addBinding();
    }
}

