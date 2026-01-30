package net.donnypz.displayentityutils.managers.holders;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncGroupHolder<T, G extends ActiveGroup<?>> extends GroupHolder<T, G>{
    protected final Object lock = new Object();


    protected AsyncGroupHolder(){
        this.groupMap = new ConcurrentHashMap<>();
    }


    @Override
    public Set<G> getGroups() {
        synchronized (lock){
            return super.getGroups();
        }
    }


    @Override
    public Set<G> getGroups(T key) {
        synchronized (lock){
            return super.getGroups(key);
        }
    }

    @Override
    public void addGroup(T key, G group) {
        groupMap.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(group);
    }
}
