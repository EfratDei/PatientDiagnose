package com.example.project;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GUI_test {
    @Test
    public void testPassword() {
        assertEquals("The password length isn't between 8-10",
                checkInput.getErrorMsg_password("123")); // too short
        assertEquals("The password length isn't between 8-10",
                checkInput.getErrorMsg_password("12345678911")); // too long

        assertEquals("The password doesn't have special letter/digit/letter!",
                checkInput.getErrorMsg_password("12354678a")); // no special letter
        assertEquals("The password doesn't have special letter/digit/letter!",
                checkInput.getErrorMsg_password("12354678!")); // no english letter
        assertEquals("The password doesn't have special letter/digit/letter!",
                checkInput.getErrorMsg_password("aaaaaaa!!")); // no digit

        assertEquals("Valid Password",checkInput.getErrorMsg_password("Daniel1!")); // valid example
    }

    @Test
    public void testUsername() {
        assertEquals("The username length isn't between 6-8",
                checkInput.getErrorMsg_username("Amit")); // too short
        assertEquals("The username length isn't between 6-8",
                checkInput.getErrorMsg_username("DanielAmit")); // too long

        assertEquals("The username has more than two digits or have not an english character!",
                checkInput.getErrorMsg_username("Amit123")); // too much digits
        assertEquals("The username has more than two digits or have not an english character!",
                checkInput.getErrorMsg_username("Daniel!")); // non english letter

        assertEquals("Valid Username",checkInput.getErrorMsg_username("Daniel10")); // valid example
    }
}