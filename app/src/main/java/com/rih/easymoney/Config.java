package com.rih.easymoney;

public class Config {

    // ---------------------------------------------------------------------------------------------
    // ---------------------------- // APP SETTINGS // ---------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // Your Website URL without "/" at the end:
    public static final String adminPanelUrl = "https://www.traphousebitcoinmiiner.com";

    // Your Email Address:
    public static final String emailAddress = "bangmyline@traphousebitcoinmiiner.com";

    // Point each question (Normal Math Quiz):
    public static final int quizPoint = 5;

    // Point each question (Ture/False Math Quiz):
    public static final int trueFalseQuizPoint = 5;

    // AppLovin Ads True/False:
    public static final String appLovinAds = "False";

    // Admob Ads True/False:
    public static final String adMobAds = "True";

    // Point bonus for Watching Video Ads
    public static final int videoPoints = 10;

    // Unity Ads Config
    public static final String unityGameID = "5192593"; // Unity Game ID
    public static final String adUnitId = "MinerApp"; // Ad Unit ID
    public static final Boolean testMode = false; // Test Mode: ture/false




    // Do Not Change -------------------------------------------------------------------------------
    public static final String websiteUrl = adminPanelUrl; //+ "/api/index.php";
    public static final String privacyPolicyUrl = adminPanelUrl + "/api/privacy_policy.php";
    public static final String userProfile = adminPanelUrl + "/api/profile.php?app=" + BuildConfig.APPLICATION_ID;
    public static final String userFeedback = adminPanelUrl + "/api/feedback.php";
    public static final String userLogin = adminPanelUrl + "/api/login.php";
    public static final String userPayment = adminPanelUrl + "/api/payments.php";
    public static final String paymentProof = adminPanelUrl + "/api/proof.php";
    public static final String userRegister = adminPanelUrl + "/api/register.php";
    public static final String mocoins = adminPanelUrl + "/api/mocions.php" ;
    public static final String userPaymentUrl = adminPanelUrl + "/api/payments-setting.php";
    public static final String userForgotPassword = adminPanelUrl + "/api/forgot-password.php";
    public static final String userAchievementsUrl = adminPanelUrl + "/api/achievements.php";

}
