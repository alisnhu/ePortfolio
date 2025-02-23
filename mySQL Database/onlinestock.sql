-- phpMyAdmin SQL Dump
-- version 4.9.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Feb 23, 2025 at 04:22 AM
-- Server version: 8.0.17
-- PHP Version: 7.3.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `onlinestock`
--
CREATE DATABASE IF NOT EXISTS `onlinestock` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `onlinestock`;

-- --------------------------------------------------------

--
-- Table structure for table `child_user`
--

DROP TABLE IF EXISTS `child_user`;
CREATE TABLE `child_user` (
  `id` int(11) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `master_user` int(11) NOT NULL,
  `admin` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Truncate table before insert `child_user`
--

TRUNCATE TABLE `child_user`;
--
-- Dumping data for table `child_user`
--



-- --------------------------------------------------------

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
  `id` int(11) NOT NULL,
  `inventory_name` text,
  `userid` int(11) DEFAULT NULL,
  `lockowner` int(11) DEFAULT NULL,
  `lockdate` datetime DEFAULT NULL,
  `rlock` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Truncate table before insert `inventory`
--

TRUNCATE TABLE `inventory`;
--
-- Dumping data for table `inventory`
--



-- --------------------------------------------------------

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id` int(11) NOT NULL,
  `name` text,
  `location` text,
  `stock` int(11) DEFAULT NULL,
  `itemnotification` tinyint(1) NOT NULL DEFAULT '0',
  `lessthan` int(11) DEFAULT NULL,
  `inventoryid` int(11) DEFAULT NULL,
  `rlock` tinyint(1) NOT NULL DEFAULT '0',
  `lockowner` int(11) DEFAULT NULL,
  `lockdate` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Truncate table before insert `item`
--

TRUNCATE TABLE `item`;
--
-- Dumping data for table `item`
--



-- --------------------------------------------------------

--
-- Stand-in structure for view `item2`
-- (See below for the actual view)
--
DROP VIEW IF EXISTS `item2`;
CREATE TABLE `item2` (
`id` int(11)
,`inventoryid` int(11)
,`itemnotification` tinyint(1)
,`lessthan` int(11)
,`location` text
,`lockdate` datetime
,`lockowner` int(11)
,`name` text
,`rlock` tinyint(1)
,`stock` int(11)
,`userid` int(11)
);

-- --------------------------------------------------------

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` int(11) NOT NULL,
  `name` text,
  `lessthan` int(11) DEFAULT NULL,
  `inventoryid` int(11) DEFAULT NULL,
  `rlock` tinyint(1) NOT NULL DEFAULT '0',
  `lockowner` int(11) DEFAULT NULL,
  `lockdate` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Truncate table before insert `notification`
--

TRUNCATE TABLE `notification`;
--
-- Dumping data for table `notification`
--



-- --------------------------------------------------------

--
-- Stand-in structure for view `notification2`
-- (See below for the actual view)
--
DROP VIEW IF EXISTS `notification2`;
CREATE TABLE `notification2` (
`id` int(11)
,`inventoryid` int(11)
,`lessthan` int(11)
,`lockdate` datetime
,`lockowner` int(11)
,`name` text
,`rlock` tinyint(1)
,`userid` int(11)
);

-- --------------------------------------------------------

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
CREATE TABLE `session` (
  `id` int(11) NOT NULL,
  `userid` int(11) DEFAULT NULL,
  `logintoken` text,
  `logindate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `validdate` datetime DEFAULT NULL,
  `child_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Truncate table before insert `session`
--

TRUNCATE TABLE `session`;
--
-- Dumping data for table `session`
--



-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `email` text,
  `phone` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Truncate table before insert `users`
--

TRUNCATE TABLE `users`;
--
-- Dumping data for table `users`
--


-- --------------------------------------------------------

--
-- Stand-in structure for view `users2`
-- (See below for the actual view)
--
DROP VIEW IF EXISTS `users2`;
CREATE TABLE `users2` (
`admin` tinyint(1)
,`child_id` int(11)
,`email` text
,`master_id` int(11)
,`password` varchar(255)
,`phone` text
,`username` varchar(255)
);

-- --------------------------------------------------------

--
-- Structure for view `item2`
--
DROP TABLE IF EXISTS `item2`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `item2`  AS  select `it`.`id` AS `id`,`it`.`name` AS `name`,`it`.`location` AS `location`,`it`.`stock` AS `stock`,`it`.`itemnotification` AS `itemnotification`,`it`.`lessthan` AS `lessthan`,`it`.`inventoryid` AS `inventoryid`,`it`.`rlock` AS `rlock`,`it`.`lockowner` AS `lockowner`,`it`.`lockdate` AS `lockdate`,`i`.`userid` AS `userid` from (`item` `it` join `inventory` `i` on((`it`.`inventoryid` = `i`.`id`))) ;

-- --------------------------------------------------------

--
-- Structure for view `notification2`
--
DROP TABLE IF EXISTS `notification2`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `notification2`  AS  select `n`.`id` AS `id`,`n`.`name` AS `name`,`n`.`lessthan` AS `lessthan`,`n`.`inventoryid` AS `inventoryid`,`n`.`rlock` AS `rlock`,`n`.`lockowner` AS `lockowner`,`n`.`lockdate` AS `lockdate`,`i`.`userid` AS `userid` from (`notification` `n` join `inventory` `i` on((`n`.`inventoryid` = `i`.`id`))) ;

-- --------------------------------------------------------

--
-- Structure for view `users2`
--
DROP TABLE IF EXISTS `users2`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `users2`  AS  select `u`.`id` AS `master_id`,`u`.`email` AS `email`,`u`.`phone` AS `phone`,`c`.`id` AS `child_id`,`c`.`username` AS `username`,`c`.`password` AS `password`,`c`.`admin` AS `admin` from (`users` `u` left join `child_user` `c` on((`u`.`id` = `c`.`master_user`))) ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `child_user`
--
ALTER TABLE `child_user`
  ADD PRIMARY KEY (`id`),
  ADD KEY `master_user` (`master_user`);

--
-- Indexes for table `inventory`
--
ALTER TABLE `inventory`
  ADD PRIMARY KEY (`id`),
  ADD KEY `userid` (`userid`);

--
-- Indexes for table `item`
--
ALTER TABLE `item`
  ADD PRIMARY KEY (`id`),
  ADD KEY `inventoryid` (`inventoryid`);

--
-- Indexes for table `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`id`),
  ADD KEY `inventoryid` (`inventoryid`);

--
-- Indexes for table `session`
--
ALTER TABLE `session`
  ADD PRIMARY KEY (`id`),
  ADD KEY `userid` (`userid`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `child_user`
--
ALTER TABLE `child_user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `inventory`
--
ALTER TABLE `inventory`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `item`
--
ALTER TABLE `item`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `notification`
--
ALTER TABLE `notification`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `session`
--
ALTER TABLE `session`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=172;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `child_user`
--
ALTER TABLE `child_user`
  ADD CONSTRAINT `child_user_ibfk_1` FOREIGN KEY (`master_user`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `inventory`
--
ALTER TABLE `inventory`
  ADD CONSTRAINT `inventory_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `item`
--
ALTER TABLE `item`
  ADD CONSTRAINT `item_ibfk_1` FOREIGN KEY (`inventoryid`) REFERENCES `inventory` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `notification`
--
ALTER TABLE `notification`
  ADD CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`inventoryid`) REFERENCES `inventory` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `session`
--
ALTER TABLE `session`
  ADD CONSTRAINT `session_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
