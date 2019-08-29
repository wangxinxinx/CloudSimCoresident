package org.iie.clodsim.drlallocation.util;

import java.io.File;

import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G5Xeon3075;

/**
 * If you are using any algorithms, policies or workload included in the power
 * package, please cite the following paper:
 *
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic
 * Algorithms and Adaptive Heuristics for Energy and Performance Efficient
 * Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency
 * and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages:
 * 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 *
 * @author Anton Beloglazov
 * @since Jan 6, 2012
 */
public class DrlConstants {

	public final static boolean ENABLE_OUTPUT = true;
	public final static boolean OUTPUT_CSV = false;
	
	public final static double CREAT_VM_INTERVEL = 60.3;//1 minute
	
	public final static int CLOUDLET_LENGTH = 1500*60*60;//million instruction 1500*60*1
//	public final static int CLOUDLET_LENGTH = 2500 * (int) SIMULATION_LIMIT;
	public final static int CLOUDLET_PES = 1;

	
	/*
	 * VM instance types:
	 *                        t3.nano    t3.medium    t2.micro    t2.large   t3.small
	 * CPU Clock Speed(GHz)     2.5        2.5          3.0         3.0         2.5
	 * vCPU                     2          2            1           2           2
	 * memory(GB)               0.5        4            1           8           2
	 * bandwidth(Gbps)          5          5            1           1
	 *   We decrease the memory size two times to enable oversubscription
	 *
	 */
	public final static int VM_TYPES	= 4;
	public final static int[] VM_MIPS	= { 2500,2500,3000,3000};
	public final static int[] VM_PES	= { 2 ,2 ,1 ,2};
	public final static int[] VM_RAM	= { 512,4096,1024,8192 };
	public final static int VM_BW		= 100000; // 100 Mbit/s
	public final static int VM_SIZE		= 2500; // 2.5 GB
	
	/*
	 * VM instance types:
	 *   High-Memory Extra Large Instance: 3.25 EC2 Compute Units, 8.55 GB // too much MIPS
	 *   High-CPU Medium Instance: 2.5 EC2 Compute Units, 0.85 GB
	 *   Extra Large Instance: 2 EC2 Compute Units, 3.75 GB
	 *   Small Instance: 1 EC2 Compute Unit, 1.7 GB
	 *   Micro Instance: 0.5 EC2 Compute Unit, 0.633 GB
	 *   We decrease the memory size two times to enable oversubscription
	 *
	 */
//	public final static int VM_TYPES	= 4;
//	public final static int[] VM_MIPS	= { 2500, 2000, 1000, 500 };
//	public final static int[] VM_PES	= { 1, 1, 1, 1 };
//	public final static int[] VM_RAM	= { 870,  1740, 1740, 613 };
//	public final static int VM_BW		= 100000; // 100 Mbit/s
//	public final static int VM_SIZE		= 2500; // 2.5 GB

	/*
	 * Host types:
	 *   HP ProLiant ML110 G4 (1 x [Xeon 3040 1860 MHz, 2 cores], 4GB)
	 *   HP ProLiant ML110 G5 (1 x [Xeon 3075 2660 MHz, 2 cores], 4GB)
	 *   
	 *     CPU Clock Speed(GHz)       CPU      memory(GB)   storage(TB)
	 *            2.5                 16           16         1
	 *            3.0                 8           32         1
	 *   
	 *   We increase the memory size to enable over-subscription (x4)
	 */
	public final static int HOST_TYPES	 = 1;
	public final static int[] HOST_MIPS	 = { 3000, 3000 };
	public final static int[] HOST_PES	 = { 32, 64 };
	public final static int[] HOST_RAM	 = { 32768, 65536 };//64G
	public final static int HOST_BW		 = 1000000; // 1 Gbit/s
	public final static int HOST_STORAGE = 1000000; // 1 TB


	public final static PowerModel[] HOST_POWER = {
		new PowerModelSpecPowerHpProLiantMl110G4Xeon3040(),
		new PowerModelSpecPowerHpProLiantMl110G5Xeon3075()
	};

//	 public final static int NUMBER_OF_HOSTS = 150;
//	 public final static int NUMBER_OF_MOST_HOST_SLOTS = 11;
//	 public final static int NUMBER_OF_VMS = 300;
//	 public final static int NUMBER_OF_USERS = 40;
//	
//	 public final static int PSSF_N = 3;
//	 public final static int PSSF_Group=20;

	public final static int NUMBER_OF_HOSTS = 100;//150 20;
	public final static int NUMBER_OF_USERS = 50;
	public final static int NUMBER_OF_VMS = 2000;//150000
	
	public final static int PSSF_N = 8;
	public final static int PSSF_Group =10 ;//30 4;

	public final static long CLOUDLET_UTILIZATION_SEED = 1;

	public final static double  UNIFORM_DISTR_UPPER_BOUND = 100000000;
	public final static UniformDistr  uniformDistr = new UniformDistr(0, UNIFORM_DISTR_UPPER_BOUND,0);
	
	public final static double MIGRATE_SCORE_THRESHOLD = 0.99;
	public final static double REALLOCATE_SCORE_THRESHOLD = 0.65;
	public final static double REALLOCATE_VM_NUM_RATIO = 0.07;
	public final static double REALLOCATE_VM_PAIR_NUM_RATIO = 0.065;

	public final static String ALLOCATION_POlICY = "crrva";//crrva drl  pssf random least most minP
	public final static String REWARD_PATH = "D:\\drl\\csv\\"+ ALLOCATION_POlICY + "_reward.csv";
	public final static String CSV_PATH = "D:" + File.separator + "drl"+File.separator + "csv" + File.separator;
	public final static String RCR_FILENAME = "rcr_"+MIGRATE_SCORE_THRESHOLD+"_"+REALLOCATE_SCORE_THRESHOLD+"_"+ REALLOCATE_VM_NUM_RATIO+"_"+REALLOCATE_VM_PAIR_NUM_RATIO+ ".csv";
	public final static String VM_SCORE_FILENAME = "vmCsore_"+MIGRATE_SCORE_THRESHOLD+"_"+REALLOCATE_SCORE_THRESHOLD+"_"+ REALLOCATE_VM_NUM_RATIO+"_"+REALLOCATE_VM_PAIR_NUM_RATIO+ ".csv";
	public final static String DRL_EXPERIENCE_FILENAME = ALLOCATION_POlICY + ".csv";
	public final static String DRL_DECISION_PATH = "D:\\drl\\csv\\drl_decision.csv";
	public final static String DRL_RESULT_PATH = "D:\\drl\\csv\\drl_results.csv";
	public final static String VM_ARRIVE_NUM_FILE = "D:\\drl\\selfsim.csv";
	

	
	public final static double ACTIVATE_COST = 10;
	
	//threat score=====================
	public final static long UPDATE_THREAT_SCORE_RANDOM_SEED = 0;
	public final static double SCAN_VM_THREAT_SCORE_INTERVAL = 5*300;
	public final static int UPDATE_THREAT_VM_NUM_ONE_TIME = (int)(NUMBER_OF_VMS*0.1);
	//threat score=====================
	
	//drl=====================
	public final static double REWARD_W1 = 0;
	public final static double REWARD_W2 = 0;
	public final static double REWARD_W3 = 0;
	public final static double REWARD_W4 = 1;
	
	public final static int AMPLIFY_OB0 = 1;
	public final static int AMPLIFY_OB1 = 1;
	public final static int AMPLIFY_OB2 = 1;
	public final static int AMPLIFY_OB3 = 1;
	
	public final static int OB_UNIT_LENGTH = 4;
	public final static int REWARD_BASE = 20;
	//drl=====================
	
	public final static double SCHEDULING_INTERVAL = 60;
	public final static double SIMULATION_LIMIT = 60*NUMBER_OF_VMS*4;
}
