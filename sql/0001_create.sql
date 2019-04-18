CREATE DATABASE `test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;

use test;

CREATE TABLE `user`
(
    `idUser` int(11) NOT NULL AUTO_INCREMENT,
    `name`   varchar(255) DEFAULT NULL,
    `email`  varchar(255) DEFAULT NULL,
    PRIMARY KEY (`idUser`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `useraddress`
(
    `idUserAddress` int(11) NOT NULL AUTO_INCREMENT,
    `idUser`        int(11) NOT NULL,
    `address`       varchar(255) DEFAULT NULL,
    PRIMARY KEY (`idUserAddress`),
    FOREIGN KEY (idUser) REFERENCES user (idUser) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE `usercontact`
(
    `idUser`    int(11) NOT NULL,
    `idContact` int(11) NOT NULL,
    PRIMARY KEY (`idUser`, `idContact`),
    FOREIGN KEY (idUser) REFERENCES user (idUser) ON DELETE CASCADE,
    FOREIGN KEY (idContact) REFERENCES user (idUser) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
