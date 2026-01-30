package net.donnypz.displayentityutils.managers.holders;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;

import java.util.*;

public class GroupHolder<T, G extends ActiveGroup<?>>{
    Map<T, Set<G>> groupMap = new HashMap<>();


    protected Set<G> getRawGroups(T key){
        return groupMap.get(key);
    }

    public Set<G> getGroups(){
        Set<G> groups = new HashSet<>();
        for (Set<G> g : groupMap.values()){
            groups.addAll(g);
        }

        return groups;
    }

    public Set<G> getGroups(T key){
        Set<G> groups = groupMap.get(key);
        if (groups == null) return Collections.emptySet();
        return new HashSet<>(groups);
    }

    public void addGroup(T key, G group){
        groupMap.computeIfAbsent(key, k -> new HashSet<>()).add(group);
    }

    public void removeGroup(T key, G group){
        Set<G> groups = groupMap.get(key);
        if (groups != null) {
            groups.remove(group);
            if (groups.isEmpty()) groupMap.remove(key);
        }
    }

    public boolean isEmpty(){
        return groupMap.isEmpty();
    }
}
