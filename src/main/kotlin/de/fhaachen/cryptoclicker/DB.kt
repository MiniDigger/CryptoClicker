package de.fhaachen.cryptoclicker

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object DB {

    private var connection: Connection

    init {
        Class.forName("com.mysql.jdbc.Driver").newInstance()
        connection = DriverManager.getConnection("jdbc:mysql://minidigger.me:3336/cryptoclicker", "cryptoclicker", "cryptoclicker")
        if (!dbExists()) createDB()
    }

    fun String.exec(): Boolean = connection.createStatement().execute(this)
    fun String.execQuery(): ResultSet = connection.createStatement().executeQuery(this)
    fun String.execUpdate(): Int = connection.createStatement().executeUpdate(this)

    private fun dbExists(): Boolean = try {
        //language=MySQL
        "SHOW TABLES LIKE 'Coin';".execQuery().next()
    } catch (ex: Exception) {
        false
    }

    private fun createDB() {
        println("Generating tables...")
        /*
        GENERATORS
         */
        //language=MySQL
        """CREATE TABLE Generator(
              id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
              name VARCHAR(20) NOT NULL,
              icon VARCHAR(60) NOT NULL,
              rate NUMERIC NOT NULL
            );""".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX Generator_id_uindex ON cryptoclicker.Generator (id);".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX Generator_name_uindex ON cryptoclicker.Generator (name);".exec()
        /*
        Upgrades
         */
        //language=MySQL
        """CREATE TABLE Upgrade
            (
              id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
              name VARCHAR(20) NOT NULL,
              icon VARCHAR(60) NOT NULL
            );""".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX Upgrade_id_uindex ON cryptoclicker.Upgrade (id);".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX Upgrade_name_uindex ON cryptoclicker.Upgrade (name);".exec()
        /*
        UserState
         */
        //language=MySQL
        """CREATE TABLE UserState
        (
            id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
            balance NUMERIC NOT NULL,
            lastSimulation BIGINT NOT NULL
        );""".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX UserState_id_uindex ON cryptoclicker.UserState (id);".exec()
        /*
        GENERATORS
         */
        //language=MySQL
        """CREATE TABLE Generators
        (
            generator_id INT,
            state_id INT,
            CONSTRAINT Generators_generator_id_state_id_pk PRIMARY KEY (generator_id, state_id),
            CONSTRAINT Generators_Generator_id_fk FOREIGN KEY (generator_id) REFERENCES cryptoclicker.Generator (id),
            CONSTRAINT Generators_UserState_id_fk FOREIGN KEY (state_id) REFERENCES cryptoclicker.UserState (id)
        );""".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX Generators_state_id_uindex ON cryptoclicker.Generators (state_id);".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX Generators_generator_id_uindex ON cryptoclicker.Generators (generator_id);".exec()
        /*
        UPGRADES
         */
        //language=MySQL
        """CREATE TABLE Upgrades
        (
            upgrade_id INT,
            state_id INT,
            CONSTRAINT Upgrades_upgrade_id_state_id_pk PRIMARY KEY (upgrade_id, state_id),
            CONSTRAINT Upgrades_Upgrade_id_fk FOREIGN KEY (upgrade_id) REFERENCES cryptoclicker.Upgrade (id),
            CONSTRAINT Upgrades_UserState_id_fk FOREIGN KEY (state_id) REFERENCES cryptoclicker.UserState (id)
        );""".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX Upgrades_state_id_uindex ON cryptoclicker.Upgrades (state_id);".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX Upgrades_upgrade_id_uindex ON cryptoclicker.Upgrades (upgrade_id);".exec()
        /*
        COIN
         */
        //language=MySQL
        """CREATE TABLE Coin
        (
            id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
            name VARCHAR(20) NOT NULL,
            icon VARCHAR(60) NOT NULL
        );""".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX Coin_id_uindex ON cryptoclicker.Coin (id);".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX Coin_name_uindex ON cryptoclicker.Coin (name);".exec()
        /*
        USER
         */
        //language=MySQL
        """CREATE TABLE User
        (
                id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
        name VARCHAR(20) NOT NULL,
        password VARCHAR(60) NOT NULL,
        email VARCHAR(60) NOT NULL,
        state_id INT NOT NULL,
        coin_id INT NOT NULL,
        CONSTRAINT User_Coin_id_fk FOREIGN KEY (coin_id) REFERENCES cryptoclicker.Coin (id),
        CONSTRAINT User_UserState_id_fk FOREIGN KEY (state_id) REFERENCES cryptoclicker.UserState (id)
        );""".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX User_id_uindex ON cryptoclicker.User (id);".exec()
        //language=MySQL
        "CREATE UNIQUE INDEX User_name_uindex ON cryptoclicker.User (name);".exec()
    }
}