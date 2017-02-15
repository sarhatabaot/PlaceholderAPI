package me.rojo8399.placeholderapi;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import me.rojo8399.placeholderapi.expansions.ConfigurableExpansion;
import me.rojo8399.placeholderapi.expansions.Expansion;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class Registry {

	private Map<String, RegistryEntry> registry = new ConcurrentHashMap<>();

	Registry() {
	}

	public boolean has(String id) {
		return registry.containsKey(id.toLowerCase());
	}

	public Set<Expansion> getAll() {
		return registry.values().stream().map(r -> r.getExpansion()).collect(Collectors.toSet());
	}

	public boolean register(Expansion e) {
		if (e == null || (!(e instanceof ConfigurableExpansion) && !e.canRegister())) {
			return false;
		}
		if (e.getIdentifier() == null || e.getIdentifier().isEmpty()) {
			return false;
		}
		if (registry.containsKey(e.getIdentifier().toLowerCase())) {
			return false;
		}
		if (e instanceof ConfigurableExpansion) {
			ConfigurableExpansion ce = (ConfigurableExpansion) e;
			ConfigurationNode node = PlaceholderAPIPlugin.getInstance().getRootConfig().getNode("expansions",
					e.getIdentifier());
			try {
				e = ce = ObjectMapper.forObject(ce).populate(node);
			} catch (ObjectMappingException e1) {
				return false;
			}
			if (!e.canRegister()) {
				return false;
			}
		}
		registry.put(e.getIdentifier(), new RegistryEntry(e));
		return true;
	}

	public Expansion get(String id) {
		if (!has(id)) {
			return null;
		}
		return getEntry(id).getExpansion();
	}

	public RegistryEntry getEntry(String id) {
		return registry.get(id.toLowerCase());
	}

}
