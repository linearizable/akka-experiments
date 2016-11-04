package io.sessionize.lib
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

case class ApacheAccessLogRequest(
    ip: String,
    datetime: DateTime,
    pid: Int,
    timetaken: Int,
    domain: String,
    size: Int,
    method: String,
    url: String,
    http: String,
    status: Int,
    referrer: String,
    userAgent: String,
    transactionId: String,
    requestId: String,
    countryCode: String
)

object ApacheAccessLogRequest {
    val rx = """^(\S+) \[(.*?)\+0530\] (\S+) (\S+) (\S+) (\S+) "(\S+) (\S+) (\S+)" (\S+) "(.*?)" "(.*?)" (\S+) (\S+) (\S+) (\S+)""".r

    def apply(logLine: String) = {
        val logElements = rx.findAllIn(logLine).matchData.next()
        val ip = logElements.group(1)
        val datetime = DateTime.parse(logElements.group(2).trim, DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss"))
        val pid = try { logElements.group(3).toInt } catch { case e:Exception => 0 }
        val timetaken = try { logElements.group(4).toInt } catch { case e:Exception => 0 }
        val domain = logElements.group(5)
        val size = try { logElements.group(6).toInt } catch { case e:Exception => 0 }
        val method = logElements.group(7)
        val url = logElements.group(8)
        val http = logElements.group(9)
        val status = try { logElements.group(10).toInt } catch { case e:Exception => 0 }
        val referrer = logElements.group(11)
        val userAgent = logElements.group(12)
        val transactionId = logElements.group(13)
        val requestId = logElements.group(14)
        val countryCode = logElements.group(15)
        new ApacheAccessLogRequest(ip, datetime, pid, timetaken, domain, size, method, url, http, status, referrer, userAgent, transactionId, requestId, countryCode)
    }
}
