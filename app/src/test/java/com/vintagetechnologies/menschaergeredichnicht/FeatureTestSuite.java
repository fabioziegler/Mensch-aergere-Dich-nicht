package com.vintagetechnologies.menschaergeredichnicht;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		BoardTest.class,
		CheatTest.class,
		DiceTest.class,
		GameLogicTest.class,
		GamePieceTest.class,
		NetworkingTest.class,
		PlayerTest.class
})

/**
 * Created by Fabio on 18.05.17.
 */
public class FeatureTestSuite {
	// the class remains empty,
	// used only as a holder for the above annotations
}