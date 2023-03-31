package me.prouge.tryjump.core.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.prouge.tryjump.core.TryJump;

public class InjectionModule extends AbstractModule {
    private final TryJump plugin;

    public InjectionModule(final TryJump plugin) {
        this.plugin = plugin;
        this.createInjector().injectMembers(this);
    }

    private Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(TryJump.class).toInstance(this.plugin);
    }

}
