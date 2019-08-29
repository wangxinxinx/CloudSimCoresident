package org.iie.cloudsim.drlallocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.iie.clodsim.drlallocation.util.DrlConstants;
import org.iie.cloudsim.util.Util;

public class RandomVmAllocationPolicy extends MyVmAllocationPolicyAbstract{

	public RandomVmAllocationPolicy(List<? extends Host> hostList, PowerVmSelectionPolicy vmSelectionPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy, utilizationThreshold);
		// TODO Auto-generated constructor stub
		
		
	}
	
	@Override
	public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
		
		PowerHost allocatedHost = null;
		List<PowerHost> candidateHosts = new ArrayList<PowerHost>();
		
		for (PowerHost host : this.<PowerHost> getHostList()) {
			
			if (excludedHosts.contains(host)) {
				continue;
			}
			if (host.isSuitableForVm(vm)) {
				if (getUtilizationOfCpuMips(host) != 0 && isHostOverUtilizedAfterAllocation(host, vm)) {
					continue;
				}

				candidateHosts.add(host);
				
			}
		}
		
		if(candidateHosts.size()!=0) {
		//	long curRealTime = System.currentTimeMillis();
			//System.out.print("\""+curRealTime+"\",");
			
			int index = (int)Util.normalize(DrlConstants.uniformDistr.sample(), 0, DrlConstants.UNIFORM_DISTR_UPPER_BOUND, 0, (double)candidateHosts.size());
			allocatedHost = candidateHosts.get(index);
		}
		
		return allocatedHost;
	}

}
