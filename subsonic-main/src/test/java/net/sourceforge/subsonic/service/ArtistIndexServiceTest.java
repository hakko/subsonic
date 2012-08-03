package net.sourceforge.subsonic.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Assert;
import org.junit.Test;

import com.github.hakko.musiccabinet.domain.model.music.Artist;

public class ArtistIndexServiceTest {

	private ArtistIndexService artistIndexService = new ArtistIndexService();
	
	@Test
	public void buildSortName() {
		Artist a = new Artist("Farrar"), b = new Artist("The Beatles");
		List<Artist> artists = Arrays.asList(a, b);
		
		artistIndexService.setArtistSortName(artists, new String[]{"THE", "EL", "LOS"});
		
		Assert.assertEquals("Farrar", a.getSortName());
		Assert.assertEquals("Beatles", b.getSortName());
	}
	
	@Test
	public void buildsLetterIndex() {
		String userInput = "A B C X-Z(XYZ)";
		Map<Character, String> map = artistIndexService.createIndexesFromExpression(userInput);
		
		Assert.assertNotNull(map);
		Assert.assertEquals(7, map.keySet().size());
		Assert.assertTrue(map.keySet().contains('A'));
		Assert.assertTrue(map.keySet().contains('B'));
		Assert.assertTrue(map.keySet().contains('C'));
		Assert.assertTrue(map.keySet().contains('X'));
		Assert.assertTrue(map.keySet().contains('Y'));
		Assert.assertTrue(map.keySet().contains('Z'));
		Assert.assertTrue(map.keySet().contains('#'));
		
		Assert.assertTrue(map.get('A').equals("A"));
		Assert.assertTrue(map.get('B').equals("B"));
		Assert.assertTrue(map.get('C').equals("C"));
		Assert.assertTrue(map.get('X').equals("X-Z"));
		Assert.assertTrue(map.get('Y').equals("X-Z"));
		Assert.assertTrue(map.get('Z').equals("X-Z"));
		Assert.assertTrue(map.get('#').equals("#"));
	}
	
	@Test
	public void groupsArtistsByIndexLetter() {
		Map<Character, String> letterIndex = new HashMap<>();
		letterIndex.put('A', "A-Y");
		letterIndex.put('Z', "Z");
		letterIndex.put('#', "#");

		List<Artist> artists = new ArrayList<>();
		artists.add(new Artist("Arkana"));
		artists.add(new Artist("Billie Idol"));
		artists.add(new Artist("Cher"));

		for (Artist artist : artists) {
			artist.setSortName(artist.getName());
		}
		
		SortedMap<String, List<Artist>> artistIndex = 
				artistIndexService.createArtistIndex(letterIndex, artists);
		
		Assert.assertEquals(2, artistIndex.keySet().size());
		Assert.assertTrue(artistIndex.containsKey("A-Y"));
		Assert.assertTrue(artistIndex.containsKey("#"));
		
		List<Artist> bcArtists = artistIndex.get("#");
		Assert.assertEquals(2, bcArtists.size());
		Assert.assertTrue(bcArtists.get(0).getName().equals("Billie Idol"));
		Assert.assertTrue(bcArtists.get(1).getName().equals("Cher"));
	}
	
	@Test
	public void sortsIndexButPutsHashLast() {
		List<String> index = new ArrayList<String>();
		index.add("A");
		index.add("#");
		index.add("C");
		index.add("B");
		
		Collections.sort(index, artistIndexService.getIndexComparator());
		
		Assert.assertEquals("A", index.get(0));
		Assert.assertEquals("B", index.get(1));
		Assert.assertEquals("C", index.get(2));
		Assert.assertEquals("#", index.get(3));
	}

}