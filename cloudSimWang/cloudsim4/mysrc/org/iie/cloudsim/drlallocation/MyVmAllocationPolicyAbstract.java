package org.iie.cloudsim.drlallocation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.iie.clodsim.drlallocation.util.DrlConstants;
import org.iie.cloudsim.util.CsvUtil;
import org.iie.cloudsim.util.Util;

//public class MyVmAllocationPolicyAbstract extends PowerVmAllocationPolicySimple {
public class MyVmAllocationPolicyAbstract extends PowerVmAllocationPolicyMigrationStaticThreshold {
	CsvUtil rcrCsv = new CsvUtil(DrlConstants.RCR_FILENAME);
	CsvUtil vmScoreCsv = new CsvUtil(DrlConstants.VM_SCORE_FILENAME);
	
	int reallocCount = 0;
	double totalRunCoThreat=0;
	int totalMigrateNum=0;

	/**
	 * The map where each key is a user id and each value are the vm ids belonging
	 * to the user.
	 */
//	 protected static Map<Integer, List<Vm>> userVmsMap = new HashMap<Integer,List<Vm>>();
	//
	// protected Map<Integer, List<Integer>> userHostIdsMap = new HashMap<Integer,
	// List<Integer>>();

	public MyVmAllocationPolicyAbstract(List<? extends Host> hostList, PowerVmSelectionPolicy vmSelectionPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy, utilizationThreshold);
		// super(hostList);
		// TODO Auto-generated constructor stub


		new AttackMonitor();

		File file = new File(DrlConstants.REWARD_PATH);
		// 如果文件已存在，则先删除已存在的文件，再创建新的文件
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		


	}


	public void finalize() {

		System.out.println(reallocCount+","+totalRunCoThreat+","+totalMigrateNum);
	}


	@Override
	public boolean allocateHostForVm(Vm vm, Host host) {
		if (host == null) {
			Log.formatLine("%.2f: No suitable host found for VM #" + vm.getId() + "\n", CloudSim.clock());
			return false;
		}
		if (host.vmCreate(vm)) { // if vm has been succesfully created in the host

			getVmTable().put(vm.getUid(), host);
			Log.formatLine("%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),
					CloudSim.clock());
			//====================================
			vm.setStartTimeInCurHost(CloudSim.clock());
//			Util.printCurLocationState(this.<PowerHost>getHostList());
			recordReward();
			//====================================
			
			return true;
		}
		Log.formatLine("%.2f: Creation of VM #" + vm.getId() + " on the host #" + host.getId() + " failed\n",
				CloudSim.clock());
		return false;
	}

	protected int getHostUserNum(Host host) {
		List<Integer> userIdInAlloHost = new ArrayList<Integer>();
		for (Vm vmtmp : host.getVmList()) {
			if (!userIdInAlloHost.contains(vmtmp.getDrlUserId())) {
				userIdInAlloHost.add(vmtmp.getDrlUserId());
			}
		}
		return userIdInAlloHost.size();
	}

	@Override
	public void deallocateHostForVm(Vm vm) {
		Host host = getVmTable().remove(vm.getUid());
		if (host != null) {

			host.vmDestroy(vm);
			recordReward();
		}
	}

	// @Override
	// public boolean allocateHostForVm(Vm vm, Host host) {
	//
	// if (host == null) {
	// Log.formatLine("%.2f: No suitable host found for VM #" + vm.getId() + "\n",
	// CloudSim.clock());
	// return false;
	// }
	// if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
	//
	// host.vmDestroy(vm);
	// double curSimTime = CloudSim.clock();
	// vm.setStartTimeInCurHost(curSimTime);
	//
	// curState = getCurrentState(vm);
	// if (preState == null) {// first allocate
	// curPwDc = (PowerDatacenter) getHostList().get(0).getDatacenter();
	//
	// } else if (curSimTime != 0) {
	// List<Object> experience = new ArrayList<Object>();
	// List<Object> fileRecord = new ArrayList<Object>();
	//
	// // action
	// experience.add(preAction);
	// fileRecord.add(preAction);
	//
	// // reward
	// experience.add(preReward);
	// fileRecord.add(preReward);
	//
	// // state_t
	// experience.addAll(preState);
	//
	// // state_t+1
	// experience.addAll(curState);
	//
	// csvUtil.writeOneLine(fileRecord);
	//
	// }
	//
	// // vm放置分割点=======================================================
	// host.vmCreate(vm);
	// getVmTable().put(vm.getUid(), host);
	// Log.formatLine("%.2f: VM #" + vm.getId() + " has been allocated to the host
	// #" + host.getId(),
	// CloudSim.clock());
	//
	// int vmDrlUserId = vm.getDrlUserId();
	//
	// int vmIndex = vm.getId() * 3;
	// ob_t.set(vmIndex, host.getId());
	// ob_t.set(vmIndex + 1, vm.getDrlUserId());
	// ob_t.set(vmIndex + 2, vm.getStartTimeInCurHost());
	//
	// // 记录状态和动作
	// preAction = host.getId();
	// preReward = calReward(curSimTime);
	// preState = curState;
	//
	// return true;
	//
	// }
	// Log.formatLine("%.2f: Creation of VM #" + vm.getId() + " on the host #" +
	// host.getId() + " failed\n",
	// CloudSim.clock());
	// return false;
	// }
	public double calTotalCoThreat() {
		int totalThreat = 0;
		for (Host host : this.getHostList()) {
			int userNum = getHostUserNum(host);
			totalThreat += userNum * (userNum - 1);
		}

		return totalThreat;
	}
	
	private int getCurUserNumInDC() {
		List<Integer> userIdList =new ArrayList<Integer>();
		for (Host host : this.getHostList()) {
			for(Vm vm:host.getVmList()) {
				if(!userIdList.contains(vm.getDrlUserId())) {
					userIdList.add(vm.getDrlUserId());
				}
			}
		}

		return userIdList.size();
	}

	protected void recordReward() {
		int curUserNum = getCurUserNumInDC();
		
		// 计算总的同驻威胁
		double totalCoThreat =0;
		if(curUserNum!=0) {
			totalCoThreat = calTotalCoThreat()/curUserNum;
		}
		
		

		// 计算所有主机虚拟机数量的标准差
		List<Double> vmNumList = new ArrayList<Double>();
		for (Host host : this.getHostList()) {
			vmNumList.add((double) host.getVmList().size());
		}
		double lbCsp = Util.getStandardDeviationForDouble(vmNumList);

		// LbT
		double lbT = 0;
		if(curUserNum!=0) {
			for (Host hostTmp : this.getHostList()) {
				Map<Integer, Integer> userVmNumMap = new HashMap<Integer, Integer>();
				for(Vm vm:hostTmp.getVmList()) {
					int drlUserId = vm.getDrlUserId();
					if(userVmNumMap.get(drlUserId)==null) {
						userVmNumMap.put(drlUserId, 1);
					}
					else {
						userVmNumMap.put(drlUserId, userVmNumMap.get(drlUserId)+1);
					}
				}
				
				for(Entry<Integer,Integer> entry:userVmNumMap.entrySet()) {
					lbT += (entry.getValue() - 1) * (entry.getValue() - 1);
				}
			}
			lbT /= curUserNum;
		}
		
		

		// 计算数据中心的总能耗(kwh)
		PowerDatacenter curPwDc = (PowerDatacenter) getHostList().get(0).getDatacenter();
		double dcEnergy = curPwDc.getPower() / (3600 * 1000);

//		try {
//			OutputStreamWriter osw = new OutputStreamWriter(
//					new FileOutputStream(new File(DrlConstants.REWARD_PATH), true), "UTF-8");// 指定以UTF-8格式写入文件
//			osw.write(String.format("%.2f,%.2f,%.2f,%.2f\n", totalCoThreat, lbCsp, lbT, dcEnergy));
//			osw.close();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	public List<Double> getCurHostCpuUtilList() {
		List<Double> curHostCpuUtilList = new ArrayList<Double>();
		for (PowerHost host : this.<PowerHost>getHostList()) {
			curHostCpuUtilList.add(host.getUtilizationOfCpu());
		}
		return curHostCpuUtilList;
	}

	class RcrPair{
		RcrPair(Vm vm1,Vm vm2,double rcr){
			setVm1(vm1);
			setVm2(vm2);
			setRcr(rcr);
			
		}
		private Vm vm1;
		
		private Vm vm2;
		private double rcr;
		
		public Vm getVm1() {
			return vm1;
		}
		public void setVm1(Vm vm1) {
			this.vm1 = vm1;
		}
		public Vm getVm2() {
			return vm2;
		}
		public void setVm2(Vm vm2) {
			this.vm2 = vm2;
		}
		public double getRcr() {
			return rcr;
		}
		public void setRcr(double rcr) {
			this.rcr = rcr;
		}
		
	}
	
	/**
	 * Optimize allocation of the VMs according to current utilization.
	 * 
	 * @param vmList the vm list
	 * 
	 * @return the array list< hash map< string, object>>
	 */
	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
//		Util.printCurLocationState(this);
		List<Vm> vmsToMigrate = new ArrayList<Vm>();
		
		//1.update threat score
		double[] vmsThreatScore = AttackMonitor.updateVmsThreatScore();
		//Log.formatLine("%.2f: VmsThreatScore:", CloudSim.clock());
		
		//2.inspect vm threat score
		int maliciousVmCount = 0;
		int suspiciousVmCount = 0;
		for(Vm vm:vmList) {
			
			if(vmsThreatScore[vm.getId()] > DrlConstants.MIGRATE_SCORE_THRESHOLD) {
				vmsToMigrate.add(vm);
				maliciousVmCount++;
			}
			
			if(vmsThreatScore[vm.getId()] > DrlConstants.REALLOCATE_SCORE_THRESHOLD) {
				suspiciousVmCount++;
			}
		}
		
		//3.calculate total run co-resident threat
		List<RcrPair> rcrPairList = new ArrayList<RcrPair>();
		double totalRcr=0;
		for(Vm vm1:vmList) {
			for(Vm vm2:vmList) {
				if((vm1.getHost().getId() == vm2.getHost().getId()) 
						&& (vm1.getId()<vm2.getId())) {
					double coLocDuration = CloudSim.clock() - (vm1.getStartTimeInCurHost()>vm2.getStartTimeInCurHost()?vm1.getStartTimeInCurHost():vm2.getStartTimeInCurHost());
					double rcr = vmsThreatScore[vm1.getId()]*coLocDuration*vmsThreatScore[vm2.getId()];
					rcrPairList.add(new RcrPair(vm1,vm2,rcr));
					totalRcr += rcr;
				}
			}
		}
		
		if(vmList.size() != 0) {
			totalRcr /= vmList.size();
		}
		
		
		//4.find suspicious vms to migrate
		Collections.sort(rcrPairList, new Comparator<RcrPair>() {
		    @Override
		    public int compare(RcrPair r1, RcrPair r2) {
			
		      double diff = r2.getRcr() - r1.getRcr();
		      if (diff > 0) {
		        return 1;
		      }else if (diff < 0) {
		        return -1;
		      }else {
		    	  return 0; //相等为0
		      }
		    }
		  }); // 按rcr降序排序
		
		if (suspiciousVmCount > vmList.size() * DrlConstants.REALLOCATE_VM_NUM_RATIO) {
			reallocCount++;
			int count=1;
			double migratePairNum = rcrPairList.size()*DrlConstants.REALLOCATE_VM_PAIR_NUM_RATIO;
			for(RcrPair rcrPair:rcrPairList) {
				if((count++) >migratePairNum ) {
					break;
				}
				
				//选威胁评分大的迁移
//				double threatScore1 = vmsThreatScore[rcrPair.getVm1().getId()];
//				double threatScore2 = vmsThreatScore[rcrPair.getVm2().getId()];
//				vmsToMigrate.add(threatScore1>threatScore2 ? rcrPair.getVm1() : rcrPair.getVm2() );
				
				//选威胁评分小的迁移
				double threatScore1 = vmsThreatScore[rcrPair.getVm1().getId()];
				double threatScore2 = vmsThreatScore[rcrPair.getVm2().getId()];
				vmsToMigrate.add(threatScore1<threatScore2 ? rcrPair.getVm1() : rcrPair.getVm2() );
				
//				vmsToMigrate.add(rcrPair.getVm1());
			}
		
		}
		//去重
		List<Vm> vmsToMigrateTmp = new ArrayList<Vm>();
		for(Vm vm:vmsToMigrate) {
			if(!vmsToMigrateTmp.contains(vm)) {
				vmsToMigrateTmp.add(vm);
			}
		} 
		vmsToMigrate.clear();   
		vmsToMigrate.addAll(vmsToMigrateTmp);
		
		//5.find new place for vms
		List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
		
		for (Vm vm : vmsToMigrate) {
//			Set<Host> excludedHosts = new HashSet<Host>();
//			if (vm.getHost() != null) {
//				excludedHosts.add(vm.getHost());
//			}
			PowerHost allocatedHost = findHostForVm(vm);
			if (allocatedHost != null) {
				Map<String, Object> migrate = new HashMap<String, Object>();
				migrate.put("vm", vm);
				migrate.put("host", allocatedHost);
				migrationMap.add(migrate);
			}
			
			vmScoreCsv.writeOneLine(Arrays.asList(vmsThreatScore[vm.getId()]));
			
		}
		
		totalRunCoThreat+=totalRcr;
		totalMigrateNum+=vmsToMigrate.size();
		
		List<Object> logData = Arrays.asList(totalRcr,vmsToMigrate.size(),maliciousVmCount);
		rcrCsv.writeOneLine(logData);
		
//		System.out.println("," + totalCoThreat + "," + vmsToMigrate.size() + "," + maliciousVmCount);
		
		migrationMap = null;
		return migrationMap;
	}
	
}
