-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema notekeeper
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema notekeeper
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `notekeeper` DEFAULT CHARACTER SET utf8 ;
USE `notekeeper` ;

-- -----------------------------------------------------
-- Table `notekeeper`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `notekeeper`.`user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `token` VARCHAR(1000) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 76
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `notekeeper`.`friendship`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `notekeeper`.`friendship` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `friend_1` INT(11) NOT NULL,
  `friend_2` INT(11) NOT NULL,
  `date_created` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `unique_index` (`friend_1` ASC, `friend_2` ASC),
  INDEX `fk_friend_1_idx` (`friend_1` ASC),
  INDEX `fk_friend_2_idx` (`friend_2` ASC),
  CONSTRAINT `fk_friend_1`
    FOREIGN KEY (`friend_1`)
    REFERENCES `notekeeper`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_friend_2`
    FOREIGN KEY (`friend_2`)
    REFERENCES `notekeeper`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 145
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `notekeeper`.`note`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `notekeeper`.`note` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `time_created` DATETIME NOT NULL,
  `user_id` INT(11) NOT NULL,
  `assigned_to` INT(11) NULL DEFAULT NULL,
  `title` VARCHAR(255) NULL DEFAULT NULL,
  `accepted` BIT(1) NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  INDEX `note_ibfk_2` (`assigned_to` ASC),
  INDEX `note_ibfk_1` (`user_id` ASC),
  CONSTRAINT `note_ibfk_1`
    FOREIGN KEY (`user_id`)
    REFERENCES `notekeeper`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `note_ibfk_2`
    FOREIGN KEY (`assigned_to`)
    REFERENCES `notekeeper`.`user` (`id`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 483
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `notekeeper`.`shared_note`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `notekeeper`.`shared_note` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `note_id` INT(11) NOT NULL,
  `user_id` INT(11) NOT NULL,
  `date_shared` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `unique_index` (`note_id` ASC, `user_id` ASC),
  CONSTRAINT `shared_note_ibfk_1`
    FOREIGN KEY (`note_id`)
    REFERENCES `notekeeper`.`note` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `notekeeper`.`task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `notekeeper`.`task` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `text` VARCHAR(255) NOT NULL,
  `note_id` INT(11) NOT NULL,
  `deadline` DATETIME NULL DEFAULT NULL,
  `checked` INT(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  INDEX `task_ibfk_1` (`note_id` ASC),
  CONSTRAINT `task_ibfk_1`
    FOREIGN KEY (`note_id`)
    REFERENCES `notekeeper`.`note` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 941
DEFAULT CHARACTER SET = utf8;

USE `notekeeper` ;

-- -----------------------------------------------------
-- Placeholder table for view `notekeeper`.`all_friendships`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `notekeeper`.`all_friendships` (`id` INT, `username` INT, `password` INT, `email` INT);

-- -----------------------------------------------------
-- View `notekeeper`.`all_friendships`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `notekeeper`.`all_friendships`;
USE `notekeeper`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`Aleksa`@`%` SQL SECURITY DEFINER VIEW `notekeeper`.`all_friendships` AS select `notekeeper`.`user`.`id` AS `id`,`notekeeper`.`user`.`username` AS `username`,`notekeeper`.`user`.`password` AS `password`,`notekeeper`.`user`.`email` AS `email` from (`notekeeper`.`user` join `notekeeper`.`friendship`) where ((`notekeeper`.`friendship`.`friend_1` = 1) or (`notekeeper`.`friendship`.`friend_2` = 1));

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;