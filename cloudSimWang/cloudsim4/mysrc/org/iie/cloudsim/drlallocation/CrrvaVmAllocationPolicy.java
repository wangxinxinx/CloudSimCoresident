package org.iie.cloudsim.drlallocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.iie.clodsim.drlallocation.util.DrlConstants;
import org.iie.cloudsim.util.Util;

public class CrrvaVmAllocationPolicy extends MyVmAllocationPolicyAbstract{

	public CrrvaVmAllocationPolicy(List<? extends Host> hostList, PowerVmSelectionPolicy vmSelectionPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy, utilizationThreshold);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Finds a PM that has enough resources to host a given VM and that will not be
	 * overloaded after placing the VM on it. The selected host will be that one
	 * with most efficient power usage for the given VM.
	 * 
	 * @param vm
	 *            the VM
	 * @param excludedHosts
	 *            the excluded hosts
	 * @return the host found to host the VM
	 */
	public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
		List<PowerHost> candidatePmList = new ArrayList<PowerHost>();
		PowerHost oldHost=(PowerHost)vm.getHost();
		
		PowerHost allocatedHost = null;
		
		for (PowerHost host : this.<PowerHost>getHostList()) {
			
			if (excludedHosts.contains(host)) {
				continue;
			}
			if (host.isSuitableForVm(vm)) {// host has enough remaining resources
				if (getUtilizationOfCpuMips(host) != 0 && isHostOverUtilizedAfterAllocation(host, vm)) {
					continue;
				}
				
				double weight = 0;
				int sameUserVmNum = 0;
				for (Vm vmtmp : host.getVmList()) {
					if (vmtmp.getDrlUserId() == vm.getDrlUserId()) {
						sameUserVmNum++;
					}
				}
				
				//alloCoThreat
				if(sameUserVmNum == 0) {
					weight += 2*getHostUserNum(host);
				}
				
				//LBcsp
				double avgVmNum = ((double) this.getVmTable().size() + 1) / DrlConstants.NUMBER_OF_HOSTS;
				weight += (host.getVmList().size() + 1 - avgVmNum) ;

				//LBt
				weight += sameUserVmNum*sameUserVmNum;
				
				//pc
				if (host.getVmList().size() == 0) {
					weight += DrlConstants.ACTIVATE_COST;
				}
				
				host.setWeight(weight);
				candidatePmList.add(host);
			}
		}
		
		Collections.sort(candidatePmList, new Comparator<PowerHost>() {
		    @Override
		    public int compare(PowerHost h1, PowerHost h2) {
			
		      double diff = h1.getWeight() - h2.getWeight();
		      if (diff > 0) {
		        return 1;
		      }else if (diff < 0) {
		        return -1;
		      }else {
		    	  return 0; //相等为0
		      }
		    }
		  }); // 按weight升序排序
		
		List<PowerHost> lowWeightHostList = new ArrayList<PowerHost>();
		double firstWeight=candidatePmList.get(0).getWeight();
		for(PowerHost hostTmp:candidatePmList) {
			if(hostTmp.getWeight()>firstWeight) {
				break;
			}
			lowWeightHostList.add(hostTmp);
		}
		
		int minVMs= Integer.MAX_VALUE;
		for(PowerHost hostTmp:lowWeightHostList) {
			if( hostTmp.getVmList().size() < minVMs)
			{
				minVMs = hostTmp.getVmList().size();
				allocatedHost = hostTmp;
			}
			
		}
		
		if(allocatedHost == null) {
			Log.printConcatLine(CloudSim.clock(), ": CrrvaVmAllocationPolicy : failed to Find host For Vm #",
					vm.getId());
		}

		if(oldHost!=null) {
			oldHost.vmCreate(vm);
		}
		return allocatedHost;
	}
}
