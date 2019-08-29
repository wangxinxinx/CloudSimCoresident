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

public class LeastVmAllocationPolicy extends MyVmAllocationPolicyAbstract{

	public LeastVmAllocationPolicy(List<? extends Host> hostList, PowerVmSelectionPolicy vmSelectionPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy, utilizationThreshold);
		// TODO Auto-generated constructor stub
		
		
	}
	
	@Override
	public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
		
		Integer MinVMs= Integer.MAX_VALUE;
		int MinVMsIndex = Integer.MAX_VALUE;
		PowerHost allocatedHost = null;
		for(int i = 0; i<this.<PowerHost> getHostList().size(); i++)
		{
			PowerHost host=  this.<PowerHost> getHostList().get(i);
			if (excludedHosts.contains(host)) {
				continue;
			}
			
			if (host.isSuitableForVm(vm)) {
				if( host.getVmList().size() < MinVMs)
				{
					MinVMs = host.getVmList().size();
					MinVMsIndex = i;
				}
			}
		}
		allocatedHost = this.<PowerHost> getHostList().get(MinVMsIndex);
		return allocatedHost;
	}

}
