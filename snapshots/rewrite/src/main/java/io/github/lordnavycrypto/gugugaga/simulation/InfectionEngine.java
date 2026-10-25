package io.github.lordnavycrypto.gugugaga.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * An actual, bounded Minecraft GAMEPLAY infection state machine.
 *
 * S -> E on contact with an infectious Minecraft animal;
 * E -> I after a configured number of game frames;
 * I -> R after another configured number of game frames.
 *
 * This models game characters, NOT real named organisms, infectious doses,
 * epidemiological rates or medically meaningful exposure.
 */
public final class InfectionEngine {
    private InfectionEngine() {}

    public enum Stage { SUSCEPTIBLE, EXPOSED, INFECTIOUS, RECOVERED }

    public record Host(UUID id, Stage stage, int age, double x,double y,double z) {
        public Host {
            Objects.requireNonNull(id);
            Objects.requireNonNull(stage);
            if(age<0 || !Double.isFinite(x) || !Double.isFinite(y)
                    || !Double.isFinite(z))
                throw new IllegalArgumentException("Invalid in-game host state");
        }
        public Host at(double nx,double ny,double nz) {
            return new Host(id,stage,age,nx,ny,nz);
        }
        public Host change(Stage next,int nextAge) {
            return new Host(id,next,nextAge,x,y,z);
        }
    }

    public record Config(int exposedFrames,int infectiousFrames,
                         double contactRadius,int maxHosts,int maxPairs) {
        public Config {
            if(exposedFrames<1||infectiousFrames<1
                    ||!Double.isFinite(contactRadius)||contactRadius<=0
                    ||contactRadius>32||maxHosts<1||maxHosts>512
                    ||maxPairs<1||maxPairs>65536)
                throw new IllegalArgumentException("Invalid game-only limits");
        }
    }

    public static final Config DEFAULT = new Config(3,8,3.5,64,4096);

    public record Frame(Map<UUID,Host> hosts,int newExposed,
                        int newlyInfectious,int newlyRecovered,int pairsChecked) {
        public Frame {
            hosts=Map.copyOf(hosts);
        }
    }

    public static Frame advance(Collection<Host> observed,Config rules) {
        Objects.requireNonNull(observed);
        Objects.requireNonNull(rules);
        if(observed.size()>rules.maxHosts())
            throw new IllegalArgumentException("Too many game animals in a frame");

        List<Host> snapshot=new ArrayList<>(observed);
        snapshot.sort(Comparator.comparing(Host::id));
        Set<UUID> unique=new HashSet<>();
        for(Host host:snapshot)
            if(!unique.add(host.id()))
                throw new IllegalArgumentException("Duplicate Minecraft animal id");

        // Contact evaluation uses the PREVIOUS frame, so newly exposed
        // animals cannot infect further animals in this same frame.
        Set<UUID> exposure=new HashSet<>();
        int pairs=0;
        double radius2=rules.contactRadius()*rules.contactRadius();
        outer:
        for(Host infectious:snapshot) {
            if(infectious.stage()!=Stage.INFECTIOUS)continue;
            for(Host susceptible:snapshot) {
                if(susceptible.stage()!=Stage.SUSCEPTIBLE)continue;
                if(pairs>=rules.maxPairs())break outer;
                pairs++;
                double dx=infectious.x()-susceptible.x();
                double dy=infectious.y()-susceptible.y();
                double dz=infectious.z()-susceptible.z();
                if(dx*dx+dy*dy+dz*dz<=radius2)
                    exposure.add(susceptible.id());
            }
        }

        Map<UUID,Host> next=new HashMap<>(snapshot.size());
        int newExposed=0,newlyInfectious=0,newlyRecovered=0;
        for(Host host:snapshot) {
            Host updated=host;
            switch(host.stage()) {
                case SUSCEPTIBLE -> {
                    if(exposure.contains(host.id())) {
                        updated=host.change(Stage.EXPOSED,0);
                        newExposed++;
                    }
                }
                case EXPOSED -> {
                    if(host.age()+1>=rules.exposedFrames()) {
                        updated=host.change(Stage.INFECTIOUS,0);
                        newlyInfectious++;
                    } else updated=host.change(Stage.EXPOSED,host.age()+1);
                }
                case INFECTIOUS -> {
                    if(host.age()+1>=rules.infectiousFrames()) {
                        updated=host.change(Stage.RECOVERED,0);
                        newlyRecovered++;
                    } else updated=host.change(Stage.INFECTIOUS,host.age()+1);
                }
                case RECOVERED -> { /* no arbitrary reinfection */ }
            }
            next.put(updated.id(),updated);
        }
        return new Frame(next,newExposed,newlyInfectious,newlyRecovered,pairs);
    }
}
