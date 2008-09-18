/*
 * Created on May 6, 2008
 * Created by Paul Gardner
 * 
 * Copyright 2008 Vuze, Inc.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License only.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package com.aelitis.azureus.core.metasearch;

import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.Entities;
import org.gudy.azureus2.core3.util.DisplayFormatters;
import org.json.simple.JSONObject;

import com.aelitis.azureus.core.metasearch.utils.MomentsAgoDateFormatter;

public abstract class Result {

	private Engine		engine;
	
	public abstract Date getPublishedDate();
	
	public abstract String getCategory();
	public abstract void setCategory(String category);
	
	public abstract String getContentType();
	public abstract void setContentType(String contentType);
	
	public abstract String getName();
	public abstract long getSize();
	public abstract int getNbPeers();
	public abstract int getNbSeeds();
	public abstract int getNbSuperSeeds();
	public abstract int getComments();
	public abstract int getVotes();
	public abstract boolean isPrivate();
	
	public abstract String getDRMKey();
	
	//Links
	public abstract String getDownloadLink();
	public abstract String getDownloadButtonLink();
	public abstract String getCDPLink();
	public abstract String getPlayLink();
	
	
	public abstract String getSearchQuery();
	
	protected
	Result(
		Engine		_engine )
	{
		engine	= _engine;
	}
	
	public Engine
	getEngine()
	{
		return( engine );
	}
	
	public String toString() {
		return getName() + " : " + getNbSeeds() + " s, " + getNbPeers() + "p, "  ;
	}
	
	
	public String getNameHTML() {
		if(getName() != null) {
			return Entities.XML.escape(getName());
		}
		return null;
	}
	
	public String getCategoryHTML() {
		if(getCategory() != null) {
			return Entities.XML.escape(getCategory());
		}
		return null;
	}
	
	/**
	 * 
	 * @return a value between 0 and 1 representing the rank of the result
	 */
	public float getRank() {

		int seeds = getNbSeeds();
		int peers = getNbPeers();
		
		if ( seeds < 0 ){
			seeds = 0;
		}
		
		if ( peers < 0 ){
			peers = 0;
		}
		
		int totalVirtualPeers = 3 * seeds + peers + 2;
		
		int superSeeds = getNbSuperSeeds();
		if(superSeeds > 0) {
			totalVirtualPeers  += 100 * superSeeds;
		}
		
		int votes = getVotes();
		if(votes > 0) {
			totalVirtualPeers += 10 * votes;
		}
		
		float rank = (float) (Math.log(totalVirtualPeers)/Math.log(10)) / 5f;
		
		if(rank > 2f) rank = 2f;
		
		if(isPrivate()) {
			rank /= 2;
		}
		
		String queryString = getSearchQuery();
		String name = getName();
		if(queryString != null && name != null) {
			name = name.toLowerCase();
			//TODO :  METASEARCH Change this as soon as Gouss sends a non escaped string
			StringTokenizer st = new StringTokenizer(queryString, " ");
			while(st.hasMoreElements()) {
				String match = st.nextToken().toLowerCase();
				if(name.indexOf(match) == -1) {
					rank /= 2;
				}
			}
		}
		
		if(rank > 1f) rank = 1f;
		
		return rank;
	}
	
	public Map toJSONMap() {
		Map object = new JSONObject();
		try {
			object.put("d", MomentsAgoDateFormatter.getMomentsAgoString(this.getPublishedDate()));
			object.put("ts", "" + this.getPublishedDate().getTime());
		} catch(Exception e) {
			object.put("d", "unknown");
			object.put("ts", "0");
		}
		
		object.put("c", this.getCategoryHTML());
		object.put("n",this.getNameHTML());
		
		if(this.getNbSeeds() >= 0) {
			object.put("s","" + this.getNbSeeds());
		} else {
			object.put("s","-1");
		}
			
		if(this.getNbPeers() >= 0) {
			object.put("p","" + this.getNbPeers());
		} else {
			object.put("p","-1");
		}
		
		int	comments = getComments();
		
		if ( comments >= 0 ){
		
			object.put( "co", "" + comments );
		}
		
		long size = this.getSize();
		if(size >= 0) {
			object.put("l", DisplayFormatters.formatByteCountToKiBEtc( size ));
			object.put("lb", "" + size  );
		} else {
			object.put("l", "-1");
			object.put("lb", "0");
		}
		
		object.put("r", "" + this.getRank());
		
		object.put("ct", this.getContentType());
		
		if ( this.getCDPLink().length() > 0 ){
			object.put("cdp", this.getCDPLink());
		}
		
			// This is also used by subscription code to extract download link so if you
			// change this you'll need to change that too...
		
		if ( this.getDownloadLink().length() > 0 ){
			object.put("dl", this.getDownloadLink());
		}
		
		if ( this.getDownloadButtonLink().length() > 0 ){
			object.put("dbl", this.getDownloadButtonLink());
		}
		
		if ( this.getPlayLink().length() > 0 ){
			object.put("pl", this.getPlayLink());
		}
		
		if ( this.getVotes() >= 0 ){
			object.put("v", "" + this.getVotes());
		}
		
		String drmKey = getDRMKey();
		if(drmKey != null) {
			object.put("dk",drmKey);
		}
		
		object.put("pr", this.isPrivate() ? "1" : "0");

		return object;
	}
	
	public static void
	adjustRelativeTerms(
		Map		map )
	{
		String	ts = (String)map.get( "ts" );
		
		if ( ts != null ){
			
			long	l_ts = Long.parseLong(ts);
			
			if ( l_ts > 0 ){
				
				map.put("d", MomentsAgoDateFormatter.getMomentsAgoString(new Date( l_ts )));

			}
		}
	}
}
