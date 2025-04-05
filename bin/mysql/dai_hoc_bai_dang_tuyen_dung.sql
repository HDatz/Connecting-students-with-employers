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
-- Table structure for table `bai_dang_tuyen_dung`
--

DROP TABLE IF EXISTS `bai_dang_tuyen_dung`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bai_dang_tuyen_dung` (
  `id_bai_dang` int NOT NULL AUTO_INCREMENT,
  `id_nha_tuyen_dung` int NOT NULL,
  `tieu_de` varchar(100) NOT NULL,
  `mo_ta` text NOT NULL,
  `dia_diem` varchar(100) DEFAULT NULL,
  `loai_cong_viec` enum('TOAN_THOI_GIAN','BAN_THOI_GIAN','THUC_TAP') NOT NULL,
  `muc_luong` varchar(50) DEFAULT NULL,
  `yeu_cau` text,
  `ngay_dang` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `han_nop` datetime(6) DEFAULT NULL,
  `trang_thai` enum('CHO_DUYET','DA_DUYET','TU_CHOI') NOT NULL,
  `so_luong_tuyen` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_bai_dang`),
  KEY `id_nha_tuyen_dung` (`id_nha_tuyen_dung`),
  KEY `idx_tieu_de` (`tieu_de`),
  KEY `idx_dia_diem` (`dia_diem`),
  CONSTRAINT `bai_dang_tuyen_dung_ibfk_1` FOREIGN KEY (`id_nha_tuyen_dung`) REFERENCES `nha_tuyen_dung` (`id_nha_tuyen_dung`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bai_dang_tuyen_dung`
--

LOCK TABLES `bai_dang_tuyen_dung` WRITE;
/*!40000 ALTER TABLE `bai_dang_tuyen_dung` DISABLE KEYS */;
/*!40000 ALTER TABLE `bai_dang_tuyen_dung` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-03 13:46:38
