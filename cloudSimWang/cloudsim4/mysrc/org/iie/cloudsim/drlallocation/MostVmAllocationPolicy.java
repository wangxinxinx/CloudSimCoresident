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

public class MostVmAllocationPolicy extends MyVmAllocationPolicyAbstract{

	public MostVmAllocationPolicy(List<? extends Host> hostList, PowerVmSelectionPolicy vmSelectionPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy, utilizationThreshold);
		// TODO Auto-generated constructor stub
		
		
	}
	
	@Override
	public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
		
		int MaxVMs= Integer.MIN_VALUE;
		int MaxVMsIndex = Integer.MIN_VALUE;
		PowerHost allocatedHost = null;
		for(int i = 0; i<this.<PowerHost> getHostList().size(); i++)
		{
			PowerHost host=  this.<PowerHost> getHostList().get(i);
			if (excludedHosts.contains(host)) {
				continue;
			}
			
			if (host.isSuitableForVm(vm)) {
				if( host.getVmList().size() > MaxVMs)
				{
					MaxVMs = host.getVmList().size();
					MaxVMsIndex = i;
				}
			}
		}
		allocatedHost = this.<PowerHost> getHostList().get(MaxVMsIndex);
		return allocatedHost;
	}

}
