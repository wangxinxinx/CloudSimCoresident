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
import org.iie.cloudsim.util.*;

public class PssfVmAllocationPolicy extends MyVmAllocationPolicyAbstract {
	

	public PssfVmAllocationPolicy(List<? extends Host> hostList, PowerVmSelectionPolicy vmSelectionPolicy,
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
				
		
		PowerHost allocatedHost = null;
		List<PowerHost> PssList = new ArrayList<PowerHost>();
		List<PowerHost> NPssList = new ArrayList<PowerHost>();
		List<PowerHost> hostList = this.<PowerHost>getHostList();

		for (PowerHost host : this.<PowerHost>getHostList()) {
			if (excludedHosts.contains(host)) {
				continue;
			}
			if (host.isSuitableForVm(vm)) {// host has enough remaining resources
				if (getUtilizationOfCpuMips(host) != 0 && isHostOverUtilizedAfterAllocation(host, vm)) {
					continue;
				}

				int matchUserCount = 0;
				for (Vm vm2 : host.getVmList()) {
					if (vm2.getDrlUserId() == vm.getDrlUserId()) {
						matchUserCount++;
					}
				}

				if ((0 < matchUserCount) && (matchUserCount <= DrlConstants.PSSF_N)) {
					PssList.add(host);
				} else {
					NPssList.add(host);
				}
			}
		}

		if (!PssList.isEmpty()) {
		//	UniformDistr uniformDistr = new UniformDistr(0, PssList.size(), DrlConstants.RANDOM_SEED4);
			int index = (int)Util.normalize(DrlConstants.uniformDistr.sample(), 0, DrlConstants.UNIFORM_DISTR_UPPER_BOUND, 0, (double)PssList.size());
			
			//int index = (int) uniformDistr.sample();
			allocatedHost = PssList.get(index);
		} else if (!NPssList.isEmpty()) {
			NPssList.sort(Comparator.naturalOrder());// °´ÕÕhostIdÅÅÐò
			int groupIndex = NPssList.get(0).getId() / DrlConstants.PSSF_Group;
			double minPower = Double.MAX_VALUE;
			
			for(PowerHost hostTmp:NPssList) {
				int hostId = hostTmp.getId();
				if( (groupIndex * DrlConstants.PSSF_Group<=hostId) 
						&& (hostId<(groupIndex + 1)* DrlConstants.PSSF_Group)) {
					if (hostTmp.getPower() < minPower) {
						minPower = hostTmp.getPower();
						allocatedHost = hostTmp;
					}
				}
			}
		
			// List<PowerHost> sortedNPsslistForCurGroup = new ArrayList<PowerHost>();
			//
			// for (PowerHost host : NPssList) {
			// if ((groupIndex * DrlConstants.PSSF_Group <= host.getId())
			// && (host.getId() < (groupIndex + 1) * DrlConstants.PSSF_Group)) {
			// if (sortedNPsslistForCurGroup.size() == 0) {
			// sortedNPsslistForCurGroup.add(host);
			// } else {
			// int i;
			// for(i=0;i<sortedNPsslistForCurGroup.size();i++) {
			// if(sortedNPsslistForCurGroup.get(i).getPower() < host.getPower()) {
			// break;
			// }
			// }
			//
			// sortedNPsslistForCurGroup.add(i, host);
			// }
			// }
			// }
			//
			//
			// double firstPower = sortedNPsslistForCurGroup.get(0).getPower();
			// int j;
			// for(j=0;j<sortedNPsslistForCurGroup.size();j++) {
			// if(sortedNPsslistForCurGroup.get(j).getPower() != firstPower) {
			// break;
			// }
			// }
			// UniformDistr uniformDistr = new UniformDistr(0,
			// j,DrlConstants.RANDOM_SEED_PSSF);
			// int selectedHostIndex= (int)uniformDistr.sample() ;
			// allocatedHost = sortedNPsslistForCurGroup.get(selectedHostIndex);
			
		} else {
			Log.printConcatLine(CloudSim.clock(), ": PssfVmAllocationPolicy : failed to Find host For Vm #",
					vm.getId());
		}

		return allocatedHost;
	}
}
