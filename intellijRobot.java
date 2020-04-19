package rsa;

import java.awt.Color;

import robocode.*;
import robocode.util.Utils;

import java.util.Random;

public class intellijRobot extends Robot  {
    public static int doubleGunCounter = 0;
    public static boolean firstHit = false;
    public static int whichSite = -1; // 0 left, 1 right
    public static boolean overLooped = false;
    int dist = 50;
    double previousEnergy = 100;
    int movementDirection = 1;
    int gunDirection = 1;
    boolean movingForward;
    static int corner = 0;

    public intellijRobot() {
    }

    public void run() {
        this.setBodyColor(Color.orange);
        this.setGunColor(Color.orange);
        this.setRadarColor(Color.red);
        this.setScanColor(Color.red);
        this.setBulletColor(Color.red);
        this.goToNearestWall();

        while(true) {
            watchNeigbourhood();
            //this.takeRandomAction();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        if (this.previousEnergy - e.getEnergy()>0) {
            // Dodge!
            this.shoot(e);
            dodgeBulletAction();
        }
        else
        {
            this.shoot(e);
        }

    }

    public void onHitByBullet(HitByBulletEvent e) {
        /*
        this.turnRight(Utils.normalRelativeAngleDegrees(90.0D - (this.getHeading() - e.getHeading())));
        this.ahead((double)this.dist);
        this.dist *= -1;
        this.scan();
        */

    }

    public void onHitRobot(HitRobotEvent e) {
        double turnGunAmt = Utils.normalRelativeAngleDegrees(e.getBearing() + this.getHeading() - this.getGunHeading());
        this.turnGunRight(turnGunAmt);
        this.fire(3.0D);
    }

    public void onHitWall(HitWallEvent e)
    {
        if(firstHit) {
            if (whichSite == 0) {
                this.turnLeft(90);
            } else {
                this.turnRight(90);
            }
        }
        else
        {
            firstHit = true;
        }
    }

    public void shoot(ScannedRobotEvent e)
    {
        double absBearing = e.getBearingRadians() + e.getHeadingRadians();
        double ex = getX() + e.getDistance() * Math.sin(absBearing);
        double ey = getY() + e.getDistance() * Math.cos(absBearing);
        double distance = Math.sqrt((this.getY() - ey) * (this.getY() - ey) + (this.getX() - ex) * (this.getX() - ex));
        double biggerValue = ((this.getBattleFieldHeight() > this.getBattleFieldWidth()) ? this.getBattleFieldHeight() : this.getBattleFieldWidth());
        double shotPower = 3 * ( 1 - distance /biggerValue);
        this.fire(shotPower);
    }

    public void goToNearestWall()
    {
        this.turnRight(Utils.normalRelativeAngleDegrees((double)corner - this.getHeading()));
        this.ahead(5000.0D);
        takeNextDirection();
    }

    public void dodgeBulletAction()
    {
        Random rand = new Random();
        switch(rand.nextInt(2))
        {
            case 0:
                this.back(200);
                break;
            case 1:
                this.ahead(200);
                break;
        }

    }

    public void reverseDirection() {
        if (this.movingForward)
        {
            this.turnLeft(180);
        }
        else
        {
            this.movingForward = true;
            this.ahead(100);
        }

    }

    public void takeNextDirection()
    {

        if(this.getY() == (this.getBattleFieldHeight() - this.getHeight() /2 ))
        {
            if(this.getX() < this.getBattleFieldWidth() /2)
            {
                this.turnRight(90);
                whichSite = 1;
            }
            else
            {
                this.turnLeft(90);
                whichSite = 0;
            }
        }
        else if( this.getY() - this.getHeight() == 0)
        {
            if(this.getX() < this.getBattleFieldWidth() /2)
            {
                this.turnLeft(90);
                whichSite = 0;
            }
            else
            {
                this.turnRight(90);
                whichSite = 1;
            }
        }
        else if(this.getX() == this.getBattleFieldWidth() - this.getWidth())
        {
            if(this.getY() < this.getBattleFieldHeight() /2)
            {
                this.turnLeft(90);
                whichSite = 0;
            }
            else
            {
                this.turnRight(90);
                whichSite = 1;
            }
        }
        else
        {
            if(this.getY() < this.getBattleFieldHeight() /2)
            {
                this.turnRight(90);
                whichSite = 1;
            }
            else
            {
                this.turnLeft(90);
                whichSite = 0;
            }
        }
    }

    public void watchNeigbourhood()
    {
        int gunRotation = 10;
        this.ahead(20);
        if(this.whichSite == 1)
        {

            if(this.doubleGunCounter > 180 || this.doubleGunCounter < 0)
            {
                overLooped = !overLooped;
            }
            if(overLooped == false)
            {
                doubleGunCounter += 10;
                this.turnGunRight(gunRotation);
            }
            else
            {
                doubleGunCounter -= 10;
                this.turnGunRight(-gunRotation);
            }

        }
        else
        {
            if(this.doubleGunCounter > 180 || this.doubleGunCounter < 0)
            {
                overLooped = !overLooped;
            }

            if(overLooped == false)
            {
                doubleGunCounter += 10;
                this.turnGunLeft(gunRotation);
            }
            else
            {
                doubleGunCounter -= 10;
                this.turnGunLeft(-gunRotation);
            }
        }
    }
}
