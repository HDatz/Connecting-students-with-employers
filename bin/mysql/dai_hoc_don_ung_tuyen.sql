-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: dai_hoc
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `don_ung_tuyen`
--

DROP TABLE IF EXISTS `don_ung_tuyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `don_ung_tuyen` (
  `id_don` int NOT NULL AUTO_INCREMENT,
  `id_bai_dang` int NOT NULL,
  `id_sinh_vien` int NOT NULL,
  `ngay_ung_tuyen` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `trang_thai` enum('Chờ duyệt','Đã xem','Đã chấp nhận','Bị từ chối') NOT NULL DEFAULT 'Chờ duyệt',
  `ngay_phan_hoi` timestamp NULL DEFAULT NULL,
  `nha_tuyen_dung_id` int DEFAULT NULL,
  `id_nha_tuyen_dung` int DEFAULT NULL,
  PRIMARY KEY (`id_don`),
  KEY `id_bai_dang` (`id_bai_dang`),
  KEY `id_sinh_vien` (`id_sinh_vien`),
  KEY `FKd94x7j0osara8yoe2182iy5gt` (`nha_tuyen_dung_id`),
  KEY `FK7xli3i37swdjgeo8sx8dpd489` (`id_nha_tuyen_dung`),
  CONSTRAINT `don_ung_tuyen_ibfk_1` FOREIGN KEY (`id_bai_dang`) REFERENCES `bai_dang_tuyen_dung` (`id_bai_dang`) ON DELETE CASCADE,
  CONSTRAINT `don_ung_tuyen_ibfk_2` FOREIGN KEY (`id_sinh_vien`) REFERENCES `sinh_vien` (`id_sinh_vien`) ON DELETE CASCADE,
  CONSTRAINT `FK7xli3i37swdjgeo8sx8dpd489` FOREIGN KEY (`id_nha_tuyen_dung`) REFERENCES `nha_tuyen_dung` (`id_nha_tuyen_dung`),
  CONSTRAINT `FKd94x7j0osara8yoe2182iy5gt` FOREIGN KEY (`nha_tuyen_dung_id`) REFERENCES `nha_tuyen_dung` (`id_nha_tuyen_dung`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `don_ung_tuyen`
--

LOCK TABLES `don_ung_tuyen` WRITE;
/*!40000 ALTER TABLE `don_ung_tuyen` DISABLE KEYS */;
/*!40000 ALTER TABLE `don_ung_tuyen` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-03 13:46:39
