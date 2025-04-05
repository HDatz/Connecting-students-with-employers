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
-- Table structure for table `binh_luan`
--

DROP TABLE IF EXISTS `binh_luan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `binh_luan` (
  `id_binh_luan` int NOT NULL AUTO_INCREMENT,
  `noi_dung` text NOT NULL,
  `id_sinh_vien` int DEFAULT NULL,
  `id_nha_tuyen_dung` int DEFAULT NULL,
  `id_quan_tri_vien` int DEFAULT NULL,
  `id_bai_viet_huong_nghiep` int DEFAULT NULL,
  `id_binh_luan_cha` int DEFAULT NULL,
  `ngay_dang` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `id_bai_viet` int DEFAULT NULL,
  PRIMARY KEY (`id_binh_luan`),
  KEY `id_sinh_vien` (`id_sinh_vien`),
  KEY `id_nha_tuyen_dung` (`id_nha_tuyen_dung`),
  KEY `id_quan_tri_vien` (`id_quan_tri_vien`),
  KEY `id_bai_viet_huong_nghiep` (`id_bai_viet_huong_nghiep`),
  KEY `id_binh_luan_cha` (`id_binh_luan_cha`),
  KEY `FKkedoax2c2anxt3w675mw8c9og` (`id_bai_viet`),
  CONSTRAINT `binh_luan_ibfk_1` FOREIGN KEY (`id_sinh_vien`) REFERENCES `sinh_vien` (`id_sinh_vien`) ON DELETE SET NULL,
  CONSTRAINT `binh_luan_ibfk_2` FOREIGN KEY (`id_nha_tuyen_dung`) REFERENCES `nha_tuyen_dung` (`id_nha_tuyen_dung`) ON DELETE SET NULL,
  CONSTRAINT `binh_luan_ibfk_3` FOREIGN KEY (`id_quan_tri_vien`) REFERENCES `quan_tri_vien` (`id_quan_tri`) ON DELETE SET NULL,
  CONSTRAINT `binh_luan_ibfk_4` FOREIGN KEY (`id_bai_viet_huong_nghiep`) REFERENCES `bai_viet_huong_nghiep` (`id_bai_viet`) ON DELETE SET NULL,
  CONSTRAINT `binh_luan_ibfk_5` FOREIGN KEY (`id_binh_luan_cha`) REFERENCES `binh_luan` (`id_binh_luan`) ON DELETE CASCADE,
  CONSTRAINT `FKkedoax2c2anxt3w675mw8c9og` FOREIGN KEY (`id_bai_viet`) REFERENCES `bai_viet_huong_nghiep` (`id_bai_viet`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `binh_luan`
--

LOCK TABLES `binh_luan` WRITE;
/*!40000 ALTER TABLE `binh_luan` DISABLE KEYS */;
/*!40000 ALTER TABLE `binh_luan` ENABLE KEYS */;
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
