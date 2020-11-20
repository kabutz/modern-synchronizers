#
# This class forms part of the Thread Safety with Phaser,
# StampedLock and VarHandle Talk by Dr Heinz Kabutz from
# JavaSpecialists.eu and may not be distributed without written
# consent.
#
# (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
#

pushd src
javac -d ../out/production/modern-synchronizers eu/javaspecialists/concurrent/playground/varhandles/Position*.java
popd
java -cp out/production/modern-synchronizers eu/javaspecialists/concurrent/playground/varhandles/PositionTest
