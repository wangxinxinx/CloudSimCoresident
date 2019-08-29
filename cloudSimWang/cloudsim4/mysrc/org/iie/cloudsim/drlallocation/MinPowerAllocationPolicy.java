package org.iie.cloudsim.drlallocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.iie.clodsim.drlallocation.util.DrlConstants;
import org.iie.cloudsim.util.Util;

public class MinPowerAllocationPolicy extends MyVmAllocationPolicyAbstract{

	public MinPowerAllocationPolicy(List<? extends Host> hostList, PowerVmSelectionPolicy vmSelectionPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy, utilizationThreshold);
		// TODO Auto-generated constructor stub
		
		
	}
	
	@Override
	public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
		
		double minPower = Double.MAX_VALUE;
	
		PowerHost allocatedHost = null;
		for(PowerHost host:this.<PowerHost> getHostList()) {
			if (excludedHosts.contains(host)) {
				continue;
			}
			
			if (host.isSuitableForVm(vm)) {
				if (host.getPower() < minPower) {
					minPower = host.getPower();
					allocatedHost = host;
				}
			}
		}
	
		return allocatedHost;
	}

}
