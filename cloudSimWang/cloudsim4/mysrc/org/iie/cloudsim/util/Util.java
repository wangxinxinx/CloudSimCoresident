package org.iie.cloudsim.util;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.power.PowerHost;

public class Util {
	public static double getStandardDeviation(List<Integer> list) {
        double ave = 0;
        for(Integer i: list)
        	ave = ave + (double)i;
        ave /= list.size();
        
        double sd = 0;
        for(Integer i: list)
            sd += ((double)(i) - ave)  * ((double)(i) - ave) ;
        sd /= list.size();
        sd =Math.sqrt(sd);
        return sd;
    }
	
	public static double getStandardDeviationForDouble(List<Double> list) {
        double ave = 0;
        for(Double i: list)
        	ave = ave + (double)i;
        ave /= list.size();
        
        double sd = 0;
        for(Double i: list)
            sd += ((double)(i) - ave)  * ((double)(i) - ave) ;
        sd /= list.size();
        sd =Math.sqrt(sd);
        return sd;
    }

	public static List<Integer> removeDuplicateIntegerList(List<Integer> list) {
	    List<Integer> newList = new ArrayList<Integer>();
	    for (Integer o : list) {
	        if (!newList .contains(o)) newList.add(o);
	    }
	    return newList;
	}
	
	public static double normalize(double fromValue,double fromLow,double fromUpper,double toLow,double toUpper) {
		return toLow + ( (fromValue-fromLow) * (toUpper-toLow) )/(fromUpper-fromLow);
	}
	
	public static void printCurLocationState(List<PowerHost> hostList) {
		//System.out.println();
		
		for(PowerHost host:hostList) {
			System.out.print(host.getVmList().size() + ", ");
		}
		System.out.println();
		for(PowerHost host:hostList) {
			System.out.print("host#" + host.getId()+ ":");
			for(Vm vm:host.getVmList()) {
				System.out.print("vm#" + vm.getId() + ",");
				System.out.print("user#" + vm.getDrlUserId()+ ",");
			}
			System.out.println();
		}
	}
	

}
