/*
 * Copyright (C) 2010- Peer internet solutions
 * 
 * This file is part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

package org.mixare.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.mixare.Marker;
import org.mixare.MixContext;
import org.mixare.MixView;
import org.mixare.NavigationMarker;
import org.mixare.POIMarker;
import org.mixare.SocialMarker;

import android.location.Location;
import android.util.Log;

/**
 * DataHandler is the model which provides the Marker Objects.
 * 
 * DataHandler is also the Factory for new Marker objects.
 */
public class DataHandler {
	
	
	private List<Marker> markerList = new ArrayList<Marker>();
	
	public void addMarkers(List<Marker> markers) {

		Log.v(MixView.TAG, "Marker before: "+markerList.size());
		for(Marker ma:markers) {
			if(!markerList.contains(ma))
				markerList.add(ma);
		}
		
		Log.d(MixView.TAG, "Marker count: "+markerList.size());
	}
	
	public void sortMarkerList() {
		Collections.sort(markerList); 
	}
	
	public void updateDistances(Location location) {
		for(Marker ma: markerList) {
			float[] dist=new float[3];
			Location.distanceBetween(ma.getLatitude(), ma.getLongitude(), location.getLatitude(), location.getLongitude(), dist);
			ma.setDistance(dist[0]);
		}
	}
	
	public void updateActivationStatus(MixContext mixContext) {
			
		Hashtable<Class, Integer> map = new Hashtable<Class, Integer>();
		Hashtable<String, Integer> url = new Hashtable<String, Integer>();
		 
		for (Marker ma : markerList) {

			Class mClass = ma.getClass();
			map.put(mClass, (map.get(mClass) != null) ? map.get(mClass) + 1 : 1);
			
			//for OpenStreetMap marker count the POIs per URL
			String strURL=ma.getOSMOriUrl();
			url.put(strURL, (url.get(strURL) != null) ? url.get(strURL) + 1 : 1);
			boolean belowURLMax = (url.get(strURL)<=ma.getOsmUrlMaxObject());
			
			boolean belowMax = (map.get(mClass) <= ma.getMaxObjects());
			boolean dataSourceSelected = mixContext.isDataSourceSelected(ma
					.getDatasource());
			
			//OpenStreetMap market set active based on 3 criterias
			//1.OpenStreetMap selected
			//2.The URL is selected
			//3.The marker is below the max number of POIs per URL
			if (ma.getDatasource().equals(DataSource.DATASOURCE.OSM)) {
				ma.setActive((belowURLMax  && dataSourceSelected && mixContext
						.isOSMUrlSelected(strURL)));
			} else {
				ma.setActive((belowMax && dataSourceSelected));
			}
		}
	}
		
	public void onLocationChanged(Location location) {
		updateDistances(location);
		sortMarkerList();
		for(Marker ma: markerList) {
			ma.update(location);
		}
	}
	
	/**
	 * @deprecated Nobody should get direct access to the list
	 */
	public List getMarkerList() {
		return markerList;
	}
	
	/**
	 * @deprecated Nobody should get direct access to the list
	 */
	public void setMarkerList(List markerList) {
		this.markerList = markerList;
	}

	public int getMarkerCount() {
		return markerList.size();
	}
	
	public Marker getMarker(int index) {
		return markerList.get(index);
	}
}
