package org.iie.cloudsim.drlallocation;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.ParetoDistr;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.iie.clodsim.drlallocation.util.DrlConstants;
import org.iie.cloudsim.util.Util;

public class AttackMonitor {
	private static double[] vmsThreatScore = new double[DrlConstants.NUMBER_OF_VMS];
	private static double[] usersThreatScore = new double[DrlConstants.NUMBER_OF_USERS];
	private static ParetoDistr paretoDistr = new ParetoDistr(new Random(20),5,1);
	private static UniformDistr  uniformDistr = new UniformDistr(-0.01, 0.01,64783);
	private static UniformDistr  uniformDistrUserIndex = new UniformDistr(0, DrlConstants.NUMBER_OF_USERS,193849);
	
	AttackMonitor(){
        
//		Log.formatLine("%.2f: VmsThreatScore:", CloudSim.clock());
		for(int i=0;i<DrlConstants.NUMBER_OF_VMS;i++) {
			double scoreTmp = paretoDistr.sample();
			while(scoreTmp>2) {
				scoreTmp = paretoDistr.sample();
			}
			vmsThreatScore[i] = scoreTmp-1;
			
//			Log.formatLine("vm" + i + ": %.2f ", scoreTmp-1);
		}
		
	}
	

	//威胁评分服从Pareto分布
	public static double[] updateVmsThreatScore() {
		
//		for(int i=0;i<DrlConstants.UPDATE_THREAT_VM_NUM_ONE_TIME;i++) {
////			double scoreTmp = paretoDistr.sample();
////			while(scoreTmp>2) {
////				scoreTmp = paretoDistr.sample();
////			}
//			
//			double scoreDiff = uniformDistr.sample();
//			int vmIndex = (int)uniformDistrUserIndex.sample();
//			
//			vmsThreatScore[vmIndex] += scoreDiff;	
//			if(vmsThreatScore[vmIndex]<0) {
//				vmsThreatScore[vmIndex] = 0;
//			}
//		}
		
		for(int i=0;i<DrlConstants.NUMBER_OF_VMS;i++) {
			double scoreTmp = paretoDistr.sample();
			while(scoreTmp>2) {
				scoreTmp = paretoDistr.sample();
			}
			vmsThreatScore[i] = scoreTmp-1;
			
//			Log.formatLine("vm" + i + ": %.2f ", scoreTmp-1);
		}
		
		return vmsThreatScore;
	}
	

	public static double[] getVmsThreatScore() {
		return vmsThreatScore;
	}

	public static void setVmsThreatScore(double[] vmsThreatScore) {
		AttackMonitor.vmsThreatScore = vmsThreatScore;
	}
	
	//TODO 归一化用户的威胁评分
	public static double[] getUsersThreatScore() {
//		Map<Integer,List<Vm>> userVmsMap = MyVmAllocationPolicyAbstract.getUserVmsMap();
//		
//		for(int i=0;i<DrlConstants.NUMBER_OF_USERS;i++) {
//			if(!userVmsMap.containsKey(i)) {
//				usersThreatScore[i]=0;
//				continue;
//			}
//			
//			List<Vm> vmList = userVmsMap.get(i);
//			
//			usersThreatScore[i]=0;
//			for(int j=0;j<vmList.size();j++) {
////				double vmTS = vmsThreatScore[vmList.get(j).getId()];
////				if(usersThreatScore[i]<vmTS) {
////					usersThreatScore[i] = vmTS;
////				}
//				
//				usersThreatScore[i]+=vmsThreatScore[vmList.get(j).getId()];
//			}
//		}
		return usersThreatScore;
	}

	public static void setUsersThreatScore(double[] usersThreatScore) {
		AttackMonitor.usersThreatScore = usersThreatScore;
	}
	

}
