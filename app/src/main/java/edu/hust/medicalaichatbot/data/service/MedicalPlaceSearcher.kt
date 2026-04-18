package edu.hust.medicalaichatbot.data.service

import edu.hust.medicalaichatbot.utils.LocationUtils
import java.util.*
import kotlin.math.*

data class MedicalPlace(
    val name: String,
    val type: String, // "Pharmacy", "Hospital", "Clinic"
    val lat: Double,
    val lon: Double,
    val address: String
)

class MedicalPlaceSearcher {
    
    // Dữ liệu mẫu (Hà Nội làm trung tâm mô phỏng)
    private val mockDatabase = listOf(
        MedicalPlace("Nhà thuốc Long Châu 1", "Pharmacy", 21.0031, 105.8431, "Giải Phóng, Hai Bà Trưng"),
        MedicalPlace("Bệnh viện Bạch Mai", "Hospital", 21.0001, 105.8401, "78 Giải Phóng"),
        MedicalPlace("Nhà thuốc Pharmacity", "Pharmacy", 21.0050, 105.8450, "Đại La, Hai Bà Trưng"),
        MedicalPlace("Phòng khám Đa khoa Quốc tế", "Clinic", 21.0100, 105.8500, "Bà Triệu"),
        MedicalPlace("Bệnh viện Việt Pháp", "Hospital", 21.0020, 105.8300, "Phương Mai"),
        MedicalPlace("Nhà thuốc An Khang", "Pharmacy", 20.9950, 105.8350, "Trường Chinh")
    )

    /**
     * Thuật toán BFS để tìm kiếm các địa điểm gần nhất.
     * Ở đây BFS duyệt qua các "ô" tọa độ (Grid) xung quanh vị trí user.
     */
    fun findNearbyPlaces(userLat: Double, userLon: Double, radiusKm: Double = 5.0): List<MedicalPlace> {
        val result = mutableListOf<MedicalPlace>()
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        val visited = mutableSetOf<Pair<Int, Int>>()

        // Chuyển tọa độ sang hệ Grid (0.01 độ ~ 1.1km)
        val startGridX = (userLat / 0.01).toInt()
        val startGridY = (userLon / 0.01).toInt()

        queue.add(Pair(startGridX, startGridY))
        visited.add(Pair(startGridX, startGridY))

        val maxGridDist = (radiusKm / 1.1).toInt() + 1

        while (queue.isNotEmpty()) {
            val (currX, currY) = queue.poll()!!
            
            // Tính khoảng cách grid
            if (abs(currX - startGridX) > maxGridDist || abs(currY - startGridY) > maxGridDist) continue

            // Tìm các địa điểm nằm trong ô grid này từ mockDatabase
            val placesInGrid = mockDatabase.filter { 
                (it.lat / 0.01).toInt() == currX && (it.lon / 0.01).toInt() == currY 
            }
            
            // Lọc thêm theo khoảng cách thực tế (Haversine formula)
            placesInGrid.forEach { place ->
                if (LocationUtils.calculateDistance(userLat, userLon, place.lat, place.lon) <= radiusKm) {
                    if (!result.contains(place)) result.add(place)
                }
            }

            // Duyệt 4 hướng (BFS)
            val directions = listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0))
            for (dir in directions) {
                val next = Pair(currX + dir.first, currY + dir.second)
                if (!visited.contains(next)) {
                    visited.add(next)
                    queue.add(next)
                }
            }
            
            if (result.size >= 5) break // Tìm đủ 5 điểm gần nhất thì dừng
        }

        return result.sortedBy { LocationUtils.calculateDistance(userLat, userLon, it.lat, it.lon) }
    }
}
