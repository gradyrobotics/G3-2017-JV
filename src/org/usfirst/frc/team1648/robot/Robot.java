package org.usfirst.frc.team1648.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	
	public static final double CONTROLLER_DEADZONE = 0.2;
	public static final double GENTLE_DRIVE_OFFSET = 0.4;
	public double autonSpeed = 0.0;
	public static final double AUTON_SPEED_GROWTH = 0.02;
	public static final double AUTON_MAX_SPEED = 0.4;
	public static final int AUTON_MAX_LOOPS = 180;
	public static final double CLIMBER_MOTOR_SPEED = 1.0;
	public static final double GENTLE_CLIMB_OFFSET = 0.4;
	public static final int GATEDROP_LOOPS = 20;
	public static final double GEARMEK_MOTOR_SPEED = -0.3;
	
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	
	boolean arcadeDrive = true;
	int sleepCounter = 0;
	int autonCounter = 0;
	int climberCounter = 0;
	int gateDropCounter = 0;
	
	Victor rightDrive1 = new Victor(1); 
	Victor rightDrive2 = new Victor (0);
	Victor leftDrive3 = new Victor(5); 
	Victor leftDrive4 = new Victor(4);
	Victor climberMotor1 = new Victor(3);
	Victor climberMotor2 = new Victor (6);
	Victor gearMekMotor = new Victor(2); // labeled Victor1
	
	XBoxController driverController = new XBoxController(0); //port closest to front
	XBoxController operatorController = new XBoxController(1);
	PowerDistributionPanel pdp = new PowerDistributionPanel(0);
		
	@Override
	public void robotInit() {
	
	}
	
	@Override
	public void autonomousPeriodic() {
		autonCenterGear();
		
		if (gateDropCounter < GATEDROP_LOOPS) {
			gearMekMotor.setSpeed(GEARMEK_MOTOR_SPEED);
			gateDropCounter++;
		} else {
			gearMekMotor.setSpeed(0.0);
		}
		
		autonCounter++;
	}
	
	@Override
	public void teleopPeriodic() {
		autonReset();
		arcadeDrive();
		
		//Arcade/tank switch
//		if (sleepCounter > 0) {
//			sleepCounter--;
//		} else if (driverController.getYButton() && driverController.getBButton()) {
//			arcadeDrive = !arcadeDrive;
//			//sleepCounter prevents rapid toggling of drive state
//			sleepCounter = 100;
//		}
		
		if (operatorController.getAButton() && operatorController.getRightTrigger()) {
			climberMotor1.setSpeed(CLIMBER_MOTOR_SPEED * GENTLE_CLIMB_OFFSET);
			climberMotor2.setSpeed(CLIMBER_MOTOR_SPEED * GENTLE_CLIMB_OFFSET);
		} else if (operatorController.getAButton()) {
			climberMotor1.setSpeed(CLIMBER_MOTOR_SPEED);
			climberMotor2.setSpeed(CLIMBER_MOTOR_SPEED);
		} else {
			climberMotor1.setSpeed(0.0);
			climberMotor2.setSpeed(0.0);
		}
		
		if (operatorController.getLeftTrigger() && operatorController.getXButton()) {
			gearMekMotor.setSpeed(GEARMEK_MOTOR_SPEED);
		} else if (operatorController.getRightTrigger() && operatorController.getXButton()) {
			gearMekMotor.setSpeed(GEARMEK_MOTOR_SPEED * -1);
		} else {
			gearMekMotor.setSpeed(0);
		}
				
		//Print out any motors that are drawing current
//		for (int c = 0; c < 16; c++) {
//			if (pdp.getCurrent(c) > 0) {
//				//System.out.println("Channel " + c + " = " + pdp.getCurrent(c));
//			}
//		}
	}
	
	public void arcadeDrive() {
		double leftYAxis = driverController.getLeftYAxis();
		double rightXAxis = driverController.getRightXAxis();
		
		if (Math.abs(leftYAxis) < CONTROLLER_DEADZONE) {
			leftYAxis = 0;
		}
		
		if (Math.abs(rightXAxis) < CONTROLLER_DEADZONE) {
			rightXAxis = 0;
		}
		
		double leftMotorSpeed = leftYAxis;
		double rightMotorSpeed = leftYAxis;
		
		rightXAxis *= 0.5;
		
		leftMotorSpeed -= rightXAxis;
		rightMotorSpeed += rightXAxis;

		
		leftMotorSpeed = capAtOne(leftMotorSpeed);
		rightMotorSpeed = capAtOne(rightMotorSpeed);
		
		leftMotorSpeed *= -1;
		
		if (driverController.getRightTrigger()) {
			leftMotorSpeed *= GENTLE_DRIVE_OFFSET;
			rightMotorSpeed *= GENTLE_DRIVE_OFFSET;
		}
		
		rightDrive1.setSpeed(rightMotorSpeed);
		rightDrive2.setSpeed(rightMotorSpeed);
		leftDrive3.setSpeed(leftMotorSpeed);
		leftDrive4.setSpeed(leftMotorSpeed);
		
		System.out.println("Right drive 1: " + rightMotorSpeed);
		System.out.println("Left drive 2: " + leftMotorSpeed);
	}
		
	public void tankDrive() {	
		double rightYAxis = driverController.getRightYAxis(); 
		double leftYAxis = driverController.getLeftYAxis(); 
		if (Math.abs(leftYAxis) < CONTROLLER_DEADZONE) {
			leftDrive3.setSpeed(0);
			leftDrive4.setSpeed(0);
		} else {
			leftDrive3.setSpeed(leftYAxis);
			leftDrive4.setSpeed(leftYAxis);
		}
		if (Math.abs(rightYAxis) < CONTROLLER_DEADZONE) {
			rightDrive1.setSpeed(0);
			rightDrive2.setSpeed(0);
		} else {
			rightDrive1.setSpeed(rightYAxis);
			rightDrive2.setSpeed(rightYAxis);
		}
	}
	
	public void autonDriveStraight() {
		double rightSpeed = 0.0;
		double leftSpeed = 0.0;
		
		if (autonCounter <= AUTON_MAX_LOOPS) {
			rightSpeed = 0.4;
			leftSpeed = 0.4;
		} else if (autonCounter > AUTON_MAX_LOOPS) {
			rightSpeed = 0;
			leftSpeed = 0;
		}
		
		//Inverting motor
		rightSpeed *= -1;
		
		leftDrive3.setSpeed(leftSpeed);
		leftDrive4.setSpeed(leftSpeed);
		rightDrive1.setSpeed(rightSpeed);
		rightDrive2.setSpeed(rightSpeed);
	}
	
	public void autonCenterGear() {
		double rightSpeed = 0.0;
		double leftSpeed = 0.0;
		
		if (autonCounter <= AUTON_MAX_LOOPS) {
			rightSpeed = 0.4;
			leftSpeed = 0.36;
		} else if (autonCounter > AUTON_MAX_LOOPS) {
			rightSpeed = 0;
			leftSpeed = 0;
		}
		
		//Inverting motor
		rightSpeed *= -1;
		
		leftDrive3.setSpeed(leftSpeed);
		leftDrive4.setSpeed(leftSpeed);
		rightDrive1.setSpeed(rightSpeed);
		rightDrive2.setSpeed(rightSpeed);
	}
	
	public static double capAtOne(double x) {
		if (x > 1.0) {
			return 1.0;
		} else if (x < -1.0) {
			return -1.0;
		}
		return x;
	}
	
	public void autonReset() {
		gateDropCounter = 0;
		autonCounter = 0;
	}
}