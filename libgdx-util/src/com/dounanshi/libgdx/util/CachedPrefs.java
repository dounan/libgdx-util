package com.dounanshi.libgdx.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Preferences;

public class CachedPrefs implements Preferences {

    /**
     * Map of key values. Null values signify the key has been removed from the
     * preferences.
     */
    private final Map<String, Object> map;

    /**
     * The actual preferences that back this cache.
     */
    private final Preferences backingPrefs;

    public CachedPrefs(Preferences backingPrefs) {
        this.map = new HashMap<String, Object>();
        this.backingPrefs = backingPrefs;
    }

    @Override
    public Preferences putBoolean(String key, boolean val) {
        map.put(key, Boolean.valueOf(val));
        return this;
    }

    @Override
    public Preferences putInteger(String key, int val) {
        map.put(key, val);
        return this;
    }

    @Override
    public Preferences putLong(String key, long val) {
        map.put(key, val);
        return this;
    }

    @Override
    public Preferences putFloat(String key, float val) {
        map.put(key, val);
        return this;
    }

    @Override
    public Preferences putString(String key, String val) {
        map.put(key, val);
        return this;
    }

    @Override
    public Preferences put(Map<String, ?> vals) {
        map.putAll(vals);
        return this;
    }

    @Override
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    @Override
    public int getInteger(String key) {
        return getInteger(key, 0);
    }

    @Override
    public long getLong(String key) {
        return getLong(key, 0);
    }

    @Override
    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    @Override
    public String getString(String key) {
        return getString(key, "");
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (map.containsKey(key)) {
            // The cache knows about the key.
            Boolean val = (Boolean) map.get(key);
            return val == null ? defValue : val;
        } else {
            // Get the value from the backingPrefs.
            boolean val = backingPrefs.getBoolean(key, defValue);
            if (backingPrefs.contains(key)) {
                // Store the value.
                map.put(key, Boolean.valueOf(val));
            }
            return val;
        }
    }

    @Override
    public int getInteger(String key, int defValue) {
        if (map.containsKey(key)) {
            // The cache knows about the key.
            Integer val = (Integer) map.get(key);
            return val == null ? defValue : val;
        } else {
            // Get the value from the backingPrefs.
            int val = backingPrefs.getInteger(key, defValue);
            if (backingPrefs.contains(key)) {
                // Store the value.
                map.put(key, val);
            }
            return val;
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        if (map.containsKey(key)) {
            // The cache knows about the key.
            Long val = (Long) map.get(key);
            return val == null ? defValue : val;
        } else {
            // Get the value from the backingPrefs.
            long val = backingPrefs.getLong(key, defValue);
            if (backingPrefs.contains(key)) {
                // Store the value.
                map.put(key, val);
            }
            return val;
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (map.containsKey(key)) {
            // The cache knows about the key.
            Float val = (Float) map.get(key);
            return val == null ? defValue : val;
        } else {
            // Get the value from the backingPrefs.
            float val = backingPrefs.getFloat(key, defValue);
            if (backingPrefs.contains(key)) {
                // Store the value.
                map.put(key, val);
            }
            return val;
        }
    }

    @Override
    public String getString(String key, String defValue) {
        if (map.containsKey(key)) {
            // The cache knows about the key.
            String val = (String) map.get(key);
            return val == null ? defValue : val;
        } else {
            // Get the value from the backingPrefs.
            String val = backingPrefs.getString(key, defValue);
            if (backingPrefs.contains(key)) {
                // Store the value.
                map.put(key, val);
            }
            return val;
        }
    }

    @Override
    public Map<String, ?> get() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(String key) {
        if (map.containsKey(key)) {
            return map.get(key) != null;
        } else {
            return backingPrefs.contains(key);
        }
    }

    @Override
    public void clear() {
        map.clear();
        backingPrefs.clear();
        backingPrefs.flush();
    }

    @Override
    public void remove(String key) {
        map.put(key, null);
    }

    @Override
    public void flush() {
        for (Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (val == null) {
                backingPrefs.remove(key);
            } else {
                if (val instanceof Boolean) {
                    backingPrefs.putBoolean(key, (Boolean) val);
                } else if (val instanceof Integer) {
                    backingPrefs.putInteger(key, (Integer) val);
                } else if (val instanceof Long) {
                    backingPrefs.putLong(key, (Long) val);
                } else if (val instanceof Float) {
                    backingPrefs.putFloat(key, (Float) val);
                } else if (val instanceof String) {
                    backingPrefs.putString(key, (String) val);
                }
            }
        }
        backingPrefs.flush();
    }
}
