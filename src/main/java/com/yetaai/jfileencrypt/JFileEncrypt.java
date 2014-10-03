/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yetaai.jfileencrypt;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author his20166
 */
public class JFileEncrypt {

    public final static String COM_ENCRYPTION = "-e";
    public final static String COM_DECRYPTION = "-d";
    public final static String COM_OPT_REPLACE = "-r";
    public final static String COM_OPT_RECURSIVE = "-R";
    public final static String COM_OUT_BEFOREFIX = "-b";
    public final static String COM_OUT_AFTERFIX = "-a";
    public final static String COM_PASSWORD = "-p";
    public final static String COM_PASSWORD_FILE = "-P";
    public final static String COM_SALT = "-s";
    public final static String COM_CERT_FILE = "-c";
    public final static String COM_ALGORITHM = "-A";
    public final static String COM_ODIR = "-O";

    public static void main(String[] args) {
        HashMap<String, IntMinMaxConstraint> hm = new HashMap();
        HashMap<String, String> hmnoco = new HashMap();
        
        ArrayList<String> al = new ArrayList();
        al.add(JFileEncrypt.COM_ENCRYPTION);
        al.add(JFileEncrypt.COM_DECRYPTION);
        al.add(JFileEncrypt.COM_OPT_REPLACE);
        al.add(JFileEncrypt.COM_OPT_RECURSIVE);
        al.add(JFileEncrypt.COM_OUT_BEFOREFIX);
        al.add(JFileEncrypt.COM_OUT_AFTERFIX);
        al.add(JFileEncrypt.COM_PASSWORD);
        al.add(JFileEncrypt.COM_PASSWORD_FILE);
        al.add(JFileEncrypt.COM_SALT);
        al.add(JFileEncrypt.COM_CERT_FILE);
        al.add(JFileEncrypt.COM_ALGORITHM);
        al.add(JFileEncrypt.COM_ODIR);

        hm.put(JFileEncrypt.COM_ENCRYPTION, new IntMinMaxConstraint(1, 10));
        hm.put(JFileEncrypt.COM_DECRYPTION, new IntMinMaxConstraint(1, 10));
        hm.put(JFileEncrypt.COM_OPT_REPLACE, new IntMinMaxConstraint(0, 0));
        hm.put(JFileEncrypt.COM_OPT_RECURSIVE, new IntMinMaxConstraint(0, 0));
        hm.put(JFileEncrypt.COM_OUT_BEFOREFIX, new IntMinMaxConstraint(1, 1));
        hm.put(JFileEncrypt.COM_OUT_AFTERFIX, new IntMinMaxConstraint());
        hm.put(JFileEncrypt.COM_PASSWORD, 1);
        hm.put(JFileEncrypt.COM_PASSWORD_FILE, 1);
        hm.put(JFileEncrypt.COM_SALT, 1);
        hm.put(JFileEncrypt.COM_CERT_FILE, 1);
        hm.put(JFileEncrypt.COM_ALGORITHM, 1);
        hm.put(JFileEncrypt.COM_ODIR, 1);

        ArrayList<String> inputfiles = new ArrayList();
        Boolean breplace = false;
        String beforefix = "";
        String afterfix = "";
        String pass = "";
        String salt = "";
        String algo = "";
        String passwordfile = "";

        String command = "";
        String homedir = System.getProperty("user.home");
        String currentdir = System.getProperty("user.dir");
        try {
            boolean command_found = false;
            for (String s : args) {
                if (s.substring(0, 1).equals("-")) {
                    if (!al.contains(s)) {
                        throw new Exception("Unkown commpan option" + s.substring(0, 2));
                    }
                }
                if (s.equals(JFileEncrypt.COM_DECRYPTION)
                        || s.equals(JFileEncrypt.COM_ENCRYPTION)) {
                    command_found = true;
                    command = s.equals(JFileEncrypt.COM_DECRYPTION)
                            ? JFileEncrypt.COM_DECRYPTION : JFileEncrypt.COM_ENCRYPTION;
                } else if (command_found && (s.equals(JFileEncrypt.COM_DECRYPTION)
                        || s.equals(JFileEncrypt.COM_ENCRYPTION))) {
                    throw new Exception("Either -d decrytion or -e encrytion possible");
                }
            }
            if (!command_found) {
                throw new Exception("Either -d decrytion or -e encrytion possible");
            }
            if (command.equals(JFileEncrypt.COM_DECRYPTION)) {
                System.out.println("Command " + "decryption" + " determined.");
            } else {
                System.out.println("Command " + "encryption" + " determined.");
            }
            //get input files which are supposed to be decrypted or encrypted
            boolean start_to_get_input_files = false;
            for (String s : args) {
                if (start_to_get_input_files && !s.substring(0, 1).equals("-")) {
                    inputfiles.add(s);
                }
                if (s.equals(command)) {
                    start_to_get_input_files = true;
                } else if (s.substring(0, 1).equals("-")) {
                    start_to_get_input_files = false;
                }
            }
            //get the output mode of either replace or not.
            int i = 0;
            for (String s : args) {
                if (i > 1) {
                    throw new Exception("Specify replace mode only once");
                }
                if (s.equals(JFileEncrypt.COM_OPT_REPLACE)) {
                    i++;
                    if (i < args.length - 1 && !args[i + 1].substring(0, 1).equals("-")) {
                        throw new Exception("No parameter for replace mode");
                    }
                }
            }
            if (i == 1) {
                breplace = true;
                System.out.println("Replace mode");
            } else {
                breplace = false;
                System.out.println("Do not Replace mode");
            }

            if (!breplace) {
                beforefix = getSingleArg(args, JFileEncrypt.COM_OUT_BEFOREFIX);
                afterfix = getSingleArg(args, JFileEncrypt.COM_OUT_AFTERFIX);
                System.out.println("Prefix: " + beforefix);
                System.out.println("Postfix: " + afterfix);
            }
            pass = getSingleArg(args, JFileEncrypt.COM_PASSWORD);
            salt = getSingleArg(args, JFileEncrypt.COM_SALT);
            algo = getSingleArg(args, JFileEncrypt.COM_ALGORITHM).replace(".", "_");
            if (pass.length() < 6) {
                throw new Exception("Too simple password");
            }
            if (salt.length() < 3) {
                throw new Exception("Too simple salt");
            }

            if (!(pass.length() > 0 || salt.length() > 0 || algo.length() > 0)) {
                passwordfile = getSingleArg(args, JFileEncrypt.COM_PASSWORD_FILE);

                File file = new File(homedir + File.separator + passwordfile);
                Scanner scanner = new Scanner(new FileReader(file));
                while (scanner.hasNextLine()) {
                    String[] line = scanner.nextLine().split("=");
                    if (line.length != 2) {
                        throw new Exception("Unknown password file");
                    }
                    if (line[0].equalsIgnoreCase("pass")) {
                        pass = line[1];
                    }
                    if (line[0].equalsIgnoreCase("salt")) {
                        salt = line[1];
                    }
                    if (line[0].equalsIgnoreCase("algo")) {
                        algo = line[1].replace('.', '_');
                    }
                }
            } else {
                if (pass.length() == 0) {
                    throw new Exception("Must specify password");
                }
                passwordfile = getSingleArg(args, JFileEncrypt.COM_PASSWORD_FILE);
                if (passwordfile.length() > 0) {
                    throw new Exception("Either password file or password/salt/algo by command");
                }
            }
            String methodname = "";
            if (command.equals(JFileEncrypt.COM_DECRYPTION)) {
                methodname = "dec_" + algo;
            } else {
                methodname = "enc_" + algo;
            }
            System.out.println("Password :" + pass);
            System.out.println("Salt: " + salt);
            System.out.println("Algo: " + algo);
            pass = pass + pass + pass + pass;
            pass = pass.substring(0, 16);
            salt = salt + salt + salt + salt + salt + salt;
            salt = salt.substring(0, 16);
            Method method;
            method = JFileEncrypt.class.getDeclaredMethod(methodname, String.class,
                    Boolean.class, String.class, String.class, String.class, String.class);
            for (String fn : inputfiles) {
                method.invoke(JFileEncrypt.class, fn, breplace,
                        beforefix, afterfix, pass, salt);
            }

            //Main loop to encrypt or decrypt each file.
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void enc_AES256_cbc(String filename, Boolean replace, String prefix, String postfix,
            String pass, String salt) {
        String ofilename = "";
        String noextfilename = filename.replaceFirst("[.][^.]+$", "");
        String extfilename = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

        if (!replace) {
            ofilename = System.getProperty("user.dir") + File.separator + prefix
                    + noextfilename + postfix + "." + extfilename;
        } else {
            ofilename = System.getProperty("user.dir") + File.separator + "_e_" + filename;
        }
        try (FileOutputStream fos = new FileOutputStream(ofilename, true)) {
            File file = new File(filename);
            byte[] b = new byte[(int) file.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(b);
            dis.close();

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING", "SunJCE");
            SecretKeySpec key = new SecretKeySpec(pass.getBytes("UTF-8"), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt.getBytes("UTF-8")));
            //System.out.println("temp encryption result:" + new String(cipher.doFinal(str.getBytes()), "UTF-8"));
            fos.write(cipher.doFinal(b));
        } catch (Exception ex) {
            Logger.getLogger(JFileEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
        if (replace) {
//            System.out.println("Replacing!");
            try {
                Files.copy(Paths.get(ofilename), Paths.get(filename), StandardCopyOption.REPLACE_EXISTING);
                Files.delete(Paths.get(ofilename));
            } catch (IOException ex) {
                Logger.getLogger(JFileEncrypt.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
    }

    public static void dec_AES256_cbc(String filename, Boolean replace, String prefix, String postfix,
            String pass, String salt) {
        String ofilename = "";
        String noextfilename = filename.replaceFirst("[.][^.]+$", "");
        String extfilename = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

        if (!replace) {
            ofilename = System.getProperty("user.dir") + File.separator + prefix
                    + noextfilename + postfix + "." + extfilename;
        } else {
            ofilename = System.getProperty("user.dir") + File.separator + "_d_" + filename;
        }
        try (FileOutputStream fos = new FileOutputStream(ofilename, true)) {
            //return;
            File file = new File(filename);
            byte[] b = new byte[(int) file.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(b);
            dis.close();
            System.out.println("Length of input:" + b.length);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING", "SunJCE");
            SecretKeySpec key = new SecretKeySpec(pass.getBytes("UTF-8"), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(salt.getBytes("UTF-8")));
            fos.write(cipher.doFinal(b));
        } catch (Exception ex) {
            Logger.getLogger(JFileEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
        if (replace) {
            try {
                Files.copy(Paths.get(ofilename), Paths.get(filename), StandardCopyOption.REPLACE_EXISTING);
                Files.delete(Paths.get(ofilename));
            } catch (IOException ex) {
                Logger.getLogger(JFileEncrypt.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
    }

    public static String getSingleArg(String args[], String lead) {
        int i0 = 0;
        String result = "";
        try {
            for (int i = 0; i < args.length; i++) {
                if (lead.equals(args[i])) {
                    i0++;
                    if (i0 == 2) {
                        throw new Exception("Specify " + lead + " only once");
                    }
                    if (i == args.length - 1) {
                        throw new Exception("Sepcify parameter after " + lead);
                    }
                    if (i == args.length - 2) {
                        if (args[i + 1].substring(0, 1).equals("-")) {
                            throw new Exception("Sepcify parameter after " + lead);
                        }
                    }
                    if (i < args.length - 2) {
                        if (args[i + 1].substring(0, 1).equals("-")) {
                            throw new Exception("Sepcify parameter after " + lead);
                        } else if (!args[i + 2].substring(0, 1).equals("-")) {
                            throw new Exception("Sepcify only one parameter after " + lead);
                        }
                    }
                    result = args[i + 1];
                }
            }
            if (i0 == 1) {
                return result;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
}
