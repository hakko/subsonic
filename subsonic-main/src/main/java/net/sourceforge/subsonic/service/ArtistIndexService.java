package net.sourceforge.subsonic.service;

import static java.lang.Character.toUpperCase;
import static org.apache.commons.lang.StringUtils.split;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.subsonic.Logger;

import org.apache.commons.lang.StringUtils;

import com.github.hakko.musiccabinet.domain.model.music.Artist;

public class ArtistIndexService {

	private SettingsService settingsService;

	private final static char HASH_CHAR = '#';
	private final static String HASH_STR = "#";
	
	private static final Logger LOG = Logger.getLogger(ArtistIndexService.class);
	
    public Map<String, List<Artist>> getIndexedArtists(List<Artist> artists) {

        setArtistSortName(artists, split(settingsService.getIgnoredArticles().toUpperCase()));
        Map<Character, String> letterIndex = createIndexesFromExpression(settingsService.getIndexString().toUpperCase());
        Map<String, List<Artist>> artistIndex = createArtistIndex(letterIndex, artists);

        for (String index : artistIndex.keySet()) {
        	Collections.sort(artistIndex.get(index), new Comparator<Artist>() {
				@Override
				public int compare(Artist a1, Artist a2) {
					return a1.getSortName().compareToIgnoreCase(a2.getSortName());
				}
			});
        }
        
        return artistIndex;
    }
    
    public SortedMap<String, List<Artist>> getIndexedArtists(List<Integer> indexes, String indexLetter, List<Artist> artists) {
    	SortedMap<String, List<Artist>> artistIndex = new TreeMap<String, List<Artist>>(getIndexComparator());
    	for (Integer index : indexes) {
			artistIndex.put(Character.toString((char) index.intValue()), null);
    	}
    	if (indexLetter != null) {
    		artistIndex.put(indexLetter, artists);
    	}
    	LOG.debug(artistIndex);
    	return artistIndex;
    }

    protected void setArtistSortName(List<Artist> artists, String[] ignoredArticles) {
    	for (Artist artist : artists) {
			artist.setSortName(artist.getName());
        	for (int i = 0; i < ignoredArticles.length; i++) {
        		if (artist.getName().toUpperCase().startsWith(ignoredArticles[i] + " ")) {
        			artist.setSortName(artist.getName().substring(ignoredArticles[i].length()+1)); // +1 for space
        		}
        	}
    	}
    }

	/**
     * Creates a list of music indexes by parsing the given expression.  The expression is a space-separated list of
     * sub-expressions, for which the rules described in {@link #createIndexFromExpression} apply.
     *
     * @param expr The expression to parse.
     * @return A list of music indexes.
     */
    protected Map<Character, String> createIndexesFromExpression(String expr) {
    	Map<Character, String> index = new LinkedHashMap<>();
    	
    	String[] expressions = StringUtils.split(expr, ' ');
    	for (String expression : expressions) {
            int separatorIndex = expression.indexOf('(');
            if (separatorIndex == -1 && expression.length() == 1) {
            	index.put(expression.charAt(0), expression);
            } else if (separatorIndex > 0) {
            	String title = expression.substring(0, separatorIndex);
            	for (int i = separatorIndex + 1; i < expression.length(); i++) {
            		if (expression.charAt(i) != ')') {
            			index.put(expression.charAt(i), title);
            		}
            	}
            }
    	}
    	if (!index.containsKey(HASH_CHAR)) {
    		index.put(HASH_CHAR, HASH_STR);
    	}

        return index;
    }
    
    protected Map<String, List<Artist>> createArtistIndex(Map<Character, String> letterIndex, List<Artist> artists) {
    	Map<String, List<Artist>> map = new LinkedHashMap<>();
    	for (String index : letterIndex.values()) {
    		map.put(index, new ArrayList<Artist>());
    	}
    	for (Artist artist : artists) {
    		char firstLetter = toUpperCase(artist.getSortName().charAt(0));
    		if (!letterIndex.containsKey(firstLetter)) {
    			firstLetter = HASH_CHAR;
    		}
    		map.get(letterIndex.get(firstLetter)).add(artist);
    	}
    	for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
    		if (map.get(it.next()).isEmpty()) {
    			it.remove();
    		}
    	}
    	
    	return map;
	}
    
    protected Comparator<String> getIndexComparator() {
    	return new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				if (s1.equals(HASH_STR) && s2.equals(HASH_STR)) {
					return 0;
				} else if (s1.equals(HASH_STR)) {
					return 1;
				} else if (s2.equals(HASH_STR)) {
					return -1;
				} else {
					return s1.compareTo(s2);
				}
			}
		};
    }

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

}