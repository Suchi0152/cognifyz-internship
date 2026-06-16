import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JOB LISTING WEB SCRAPER - LEVEL 3 TASK 6
 * Cognifyz Internship Program
 * Java Version
 * 
 * Features: Scrape job listings, filter by keyword,
 *           save to CSV, user-friendly display
 */
public class JobScraper {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    private static final int REQUEST_DELAY = 2000; // 2 seconds in milliseconds
    
    // ==============================================
    // STEP 1: SELECT WEBSITE AND IDENTIFY DATA
    // ==============================================
    
    /**
     * JobListing class to store job listing data
     */
    static class JobListing {
        String title;
        String company;
        String location;
        String salary;
        String description;
        String url;
        String postedDate;
        String scrapedDate;
        
        JobListing(String title, String company, String location, String salary, 
                   String description, String url, String postedDate) {
            this.title = title;
            this.company = company;
            this.location = location;
            this.salary = salary;
            this.description = description;
            this.url = url;
            this.postedDate = postedDate;
            this.scrapedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        void displayShort() {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📌 " + title);
            System.out.println("🏢 " + company + " | 📍 " + location);
            if (salary != null && !salary.isEmpty() && !salary.equals("Not specified")) {
                System.out.println("💰 " + salary);
            }
            System.out.println("📅 Posted: " + postedDate);
            System.out.println("🔗 " + (url.length() > 80 ? url.substring(0, 80) + "..." : url));
            System.out.println("📝 " + (description.length() > 150 ? description.substring(0, 150) + "..." : description));
            System.out.println("=".repeat(60));
        }
        
        Map<String, String> toMap() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("title", title);
            map.put("company", company);
            map.put("location", location);
            map.put("salary", salary);
            map.put("description", description);
            map.put("url", url);
            map.put("posted_date", postedDate);
            map.put("scraped_date", scrapedDate);
            return map;
        }
    }
    
    // ==============================================
    // DEMO SCRAPER (RemoteOK.io)
    // ==============================================
    
    /**
     * Scrape jobs from RemoteOK.io
     * This site has a simple structure and is scraper-friendly
     */
    static List<JobListing> scrapeRemoteOkJobs(String jobTitle) {
        System.out.println("\n🌐 Scraping RemoteOK.io for jobs...");
        
        String url;
        if (jobTitle != null && !jobTitle.isEmpty()) {
            url = "https://remoteok.io/remote-dev+" + jobTitle.replace(" ", "-") + "-jobs";
        } else {
            url = "https://remoteok.io/remote-dev-jobs";
        }
        
        try {
            String html = fetchPage(url);
            List<JobListing> jobs = parseRemoteOkHtml(html);
            System.out.println("✅ Found " + jobs.size() + " jobs");
            return jobs;
        } catch (IOException | InterruptedException e) {
            System.out.println("❌ Error fetching jobs: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    static String fetchPage(String urlString) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        
        if (response.statusCode() != 200) {
            throw new IOException("HTTP response code: " + response.statusCode());
        }
        
        return response.body();
    }
    
    static List<JobListing> parseRemoteOkHtml(String html) {
        List<JobListing> jobs = new ArrayList<>();
        
        // Simple regex-based parsing (since we can't use BeautifulSoup in Java)
        // Extract job cards using patterns
        String[] jobCards = html.split("<tr class=\"job\"");
        
        int count = 0;
        for (String card : jobCards) {
            if (count >= 20) break;
            if (card.length() < 100) continue;
            
            try {
                String title = extractBetween(card, "<h2 itemprop=\"title\">", "</h2>");
                if (title.isEmpty()) continue;
                
                String company = extractBetween(card, "<h3 itemprop=\"name\">", "</h3>");
                if (company.isEmpty()) company = "N/A";
                
                String location = "Remote (Worldwide)";
                
                String salary = extractBetween(card, "<div class=\"location\">", "</div>");
                if (salary.isEmpty()) salary = "Not specified";
                
                String description = extractBetween(card, "<div class=\"description\">", "</div>");
                if (description.isEmpty()) description = "No description";
                description = cleanHtml(description).substring(0, Math.min(300, description.length()));
                
                String urlSuffix = extractBetween(card, "<a class=\"preventLink\" href=\"", "\"");
                String jobUrl = urlSuffix.isEmpty() ? "N/A" : "https://remoteok.io" + urlSuffix;
                
                String postedDate = extractBetween(card, "<td class=\"time\">", "</td>");
                if (postedDate.isEmpty()) postedDate = "Recently";
                
                JobListing job = new JobListing(title, company, location, salary, description, jobUrl, postedDate);
                jobs.add(job);
                count++;
                
            } catch (Exception e) {
                // Skip this card - specific exception handling not needed
            }
        }
        
        return jobs;
    }
    
    static String extractBetween(String text, String start, String end) {
        int startIdx = text.indexOf(start);
        if (startIdx == -1) return "";
        int endIdx = text.indexOf(end, startIdx + start.length());
        if (endIdx == -1) return "";
        return text.substring(startIdx + start.length(), endIdx).trim();
    }
    
    static String cleanHtml(String html) {
        return html.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }
    
    // ==============================================
    // DEMO SCRAPER 2: Mock Indeed-like Scraper
    // ==============================================
    
    static class DemoJobScraper {
        
        static List<JobListing> getSampleJobs() {
            List<JobListing> jobs = new ArrayList<>();
            jobs.add(new JobListing(
                "Senior Python Developer",
                "TechCorp Solutions",
                "New York, NY (Remote)",
                "$120,000 - $150,000",
                "Looking for an experienced Python developer with 5+ years experience in Django and FastAPI. Must have strong database skills.",
                "https://example.com/job/1",
                "2 days ago"
            ));
            jobs.add(new JobListing(
                "Data Scientist",
                "Analytics Pro",
                "San Francisco, CA",
                "$130,000 - $160,000",
                "Seeking data scientist with ML experience. Python, TensorFlow, and SQL required.",
                "https://example.com/job/2",
                "1 day ago"
            ));
            jobs.add(new JobListing(
                "Frontend React Developer",
                "Web Innovations",
                "Austin, TX (Hybrid)",
                "$100,000 - $130,000",
                "React, TypeScript, and Next.js expert needed for exciting web projects.",
                "https://example.com/job/3",
                "3 days ago"
            ));
            jobs.add(new JobListing(
                "DevOps Engineer",
                "Cloud Systems Inc",
                "Seattle, WA",
                "$115,000 - $145,000",
                "AWS, Docker, Kubernetes experience required. CI/CD pipeline management.",
                "https://example.com/job/4",
                "Yesterday"
            ));
            jobs.add(new JobListing(
                "Full Stack JavaScript Developer",
                "Startup Hub",
                "Remote (US only)",
                "$90,000 - $120,000",
                "Node.js, React, MongoDB. Fast-paced startup environment.",
                "https://example.com/job/5",
                "5 days ago"
            ));
            return jobs;
        }
        
        static List<JobListing> scrapeIndeed(String jobTitle) {
            System.out.println("\n🔍 Simulating Indeed scrape for '" + jobTitle + "'...");
            System.out.println("💡 Note: Using sample data for demonstration");
            System.out.println("   Real implementation would require rotating proxies, cookies, etc.\n");
            return getSampleJobs();
        }
    }
    
    // ==============================================
    // STEP 2: UTILIZE WEB SCRAPING LIBRARY
    // ==============================================
    
    static List<JobListing> scrapeJobsFromMultipleSources(String jobTitle) {
        List<JobListing> allJobs = new ArrayList<>();
        
        // Source 1: RemoteOK
        System.out.println("\n📡 SOURCE 1: RemoteOK.io");
        List<JobListing> remoteJobs = scrapeRemoteOkJobs(jobTitle);
        allJobs.addAll(remoteJobs);
        
        // Delay between sources
        sleepSafely(REQUEST_DELAY);
        
        // Source 2: Demo Indeed scraper
        System.out.println("\n📡 SOURCE 2: Indeed (Demo)");
        List<JobListing> indeedJobs = DemoJobScraper.scrapeIndeed(jobTitle);
        allJobs.addAll(indeedJobs);
        
        return allJobs;
    }
    
    private static void sleepSafely(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }
    
    // ==============================================
    // STEP 3: DESIGN USER-FRIENDLY PRESENTATION
    // ==============================================
    
    static boolean displayJobsMenu(List<JobListing> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            System.out.println("\n❌ No jobs found!");
            return false;
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📊 JOB LISTINGS SUMMARY (" + jobs.size() + " jobs found)");
        System.out.println("=".repeat(70));
        
        for (int i = 0; i < jobs.size(); i++) {
            JobListing job = jobs.get(i);
            System.out.println("\n" + (i + 1) + ". " + job.title);
            System.out.println("   🏢 " + job.company + " | 📍 " + job.location);
            if (job.salary != null && !job.salary.isEmpty() && !job.salary.equals("Not specified")) {
                System.out.println("   💰 " + job.salary);
            }
            System.out.println("   📅 Posted: " + job.postedDate);
        }
        
        return true;
    }
    
    static void showJobDetails(List<JobListing> jobs) {
        if (jobs == null || jobs.isEmpty()) return;
        
        while (true) {
            try {
                System.out.print("\n🔍 Enter job number to view details (1-" + jobs.size() + ") or 0 to go back: ");
                String input = scanner.nextLine().trim();
                if (input.equals("0")) break;
                
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < jobs.size()) {
                    jobs.get(idx).displayShort();
                } else {
                    System.out.println("❌ Invalid choice. Please enter 1-" + jobs.size());
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number");
            }
        }
    }
    
    static List<JobListing> filterJobsByKeyword(List<JobListing> jobs, String keyword) {
        String keywordLower = keyword.toLowerCase();
        List<JobListing> filtered = new ArrayList<>();
        
        for (JobListing job : jobs) {
            if (job.title.toLowerCase().contains(keywordLower) || 
                job.description.toLowerCase().contains(keywordLower)) {
                filtered.add(job);
            }
        }
        
        return filtered;
    }
    
    static List<JobListing> filterJobsByLocation(List<JobListing> jobs, String location) {
        String locationLower = location.toLowerCase();
        List<JobListing> filtered = new ArrayList<>();
        
        for (JobListing job : jobs) {
            if (job.location.toLowerCase().contains(locationLower)) {
                filtered.add(job);
            }
        }
        
        return filtered;
    }
    
    static void saveJobsToCsv(List<JobListing> jobs, String filename) {
        if (jobs == null || jobs.isEmpty()) {
            System.out.println("❌ No jobs to save");
            return;
        }
        
        // Use try-with-resources for automatic resource management
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            Map<String, String> sample = jobs.get(0).toMap();
            writer.println(String.join(",", sample.keySet()));
            
            // Write data
            for (JobListing job : jobs) {
                Map<String, String> data = job.toMap();
                List<String> values = new ArrayList<>();
                for (String key : sample.keySet()) {
                    String value = data.getOrDefault(key, "");
                    // Escape commas and quotes
                    if (value.contains(",") || value.contains("\"")) {
                        value = "\"" + value.replace("\"", "\"\"") + "\"";
                    }
                    values.add(value);
                }
                writer.println(String.join(",", values));
            }
            
            System.out.println("✅ Saved " + jobs.size() + " jobs to '" + filename + "'");
        } catch (IOException e) {
            System.out.println("❌ Error saving to CSV: " + e.getMessage());
        }
    }
    
    static void saveJobsToJson(List<JobListing> jobs, String filename) {
        if (jobs == null || jobs.isEmpty()) {
            System.out.println("❌ No jobs to save");
            return;
        }
        
        // Use try-with-resources for automatic resource management
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("[");
            for (int i = 0; i < jobs.size(); i++) {
                Map<String, String> data = jobs.get(i).toMap();
                writer.println("  {");
                int j = 0;
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    writer.print("    \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"");
                    if (j < data.size() - 1) writer.print(",");
                    writer.println();
                    j++;
                }
                writer.print("  }");
                if (i < jobs.size() - 1) writer.print(",");
                writer.println();
            }
            writer.println("]");
            
            System.out.println("✅ Saved " + jobs.size() + " jobs to '" + filename + "'");
        } catch (IOException e) {
            System.out.println("❌ Error saving to JSON: " + e.getMessage());
        }
    }
    
    static void generateStatisticsReport(List<JobListing> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            System.out.println("❌ No data to generate statistics");
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 JOB MARKET STATISTICS REPORT");
        System.out.println("=".repeat(60));
        
        System.out.println("\n📌 Total Jobs Found: " + jobs.size());
        
        // Companies count
        Map<String, Integer> companies = new HashMap<>();
        Map<String, Integer> locations = new HashMap<>();
        
        for (JobListing job : jobs) {
            companies.put(job.company, companies.getOrDefault(job.company, 0) + 1);
            String city = job.location.contains(",") ? job.location.split(",")[0].trim() : job.location;
            locations.put(city, locations.getOrDefault(city, 0) + 1);
        }
        
        System.out.println("\n🏢 Top Companies Hiring:");
        companies.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> System.out.println("   • " + entry.getKey() + ": " + entry.getValue() + " job(s)"));
        
        System.out.println("\n📍 Top Locations:");
        locations.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> System.out.println("   • " + entry.getKey() + ": " + entry.getValue() + " job(s)"));
        
        // Salary analysis
        List<Integer> salaries = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        for (JobListing job : jobs) {
            if (job.salary != null && !job.salary.isEmpty() && !job.salary.equals("Not specified")) {
                Matcher matcher = pattern.matcher(job.salary);
                if (matcher.find()) {
                    int num = Integer.parseInt(matcher.group());
                    if (num < 1000) {
                        salaries.add(num * 1000);
                    } else {
                        salaries.add(num);
                    }
                }
            }
        }
        
        if (!salaries.isEmpty()) {
            int sum = 0;
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (int s : salaries) {
                sum += s;
                if (s < min) min = s;
                if (s > max) max = s;
            }
            int avg = sum / salaries.size();
            System.out.println("\n💰 Average Salary: $" + avg);
            System.out.println("   Salary Range: $" + min + " - $" + max);
        }
        
        System.out.println("=".repeat(60));
    }
    
    // ==============================================
    // STEP 4: TEST WITH DIFFERENT WEBSITES
    // ==============================================
    
    static void testWithDifferentSites() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TESTING SCRAPER WITH DIFFERENT CONFIGURATIONS");
        System.out.println("=".repeat(60));
        
        String[][] testCases = {
            {"All Jobs", ""},
            {"Python Jobs", "python"},
            {"JavaScript Jobs", "javascript"},
            {"Remote Jobs", "remote"}
        };
        
        for (String[] test : testCases) {
            System.out.println("\n📋 TEST: " + test[0]);
            System.out.println("-".repeat(40));
            
            List<JobListing> jobs = scrapeJobsFromMultipleSources(test[1]);
            System.out.println("✅ Results: " + jobs.size() + " jobs found");
            
            sleepSafely(REQUEST_DELAY);
        }
    }
    
    // ==============================================
    // INTERACTIVE SCRAPER
    // ==============================================
    
    static void interactiveScraper() {
        List<JobListing> currentJobs = new ArrayList<>();
        
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("💼 JOB LISTING WEB SCRAPER 💼");
            System.out.println("=".repeat(60));
            System.out.println("1. 🔍 Scrape Jobs (Default)");
            System.out.println("2. 🔎 Scrape Jobs with Keyword (e.g., Python)");
            System.out.println("3. 📋 View All Jobs");
            System.out.println("4. 🔍 Filter by Keyword (in title/description)");
            System.out.println("5. 📍 Filter by Location");
            System.out.println("6. 📊 View Statistics Report");
            System.out.println("7. 💾 Save Jobs to CSV");
            System.out.println("8. 💾 Save Jobs to JSON");
            System.out.println("9. 🧪 Run Test with Different Websites");
            System.out.println("10. ❌ Exit");
            System.out.println("=".repeat(60));
            
            System.out.print("\nEnter your choice (1-10): ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> {
                    System.out.println("\n🔄 Scraping jobs... (This may take a few seconds)");
                    currentJobs = scrapeJobsFromMultipleSources("");
                    displayJobsMenu(currentJobs);
                }
                case "2" -> {
                    System.out.print("Enter job keyword (e.g., Python, JavaScript): ");
                    String keyword = scanner.nextLine().trim();
                    System.out.println("\n🔄 Scraping jobs for '" + keyword + "'...");
                    currentJobs = scrapeJobsFromMultipleSources(keyword);
                    displayJobsMenu(currentJobs);
                }
                case "3" -> {
                    if (!currentJobs.isEmpty()) {
                        displayJobsMenu(currentJobs);
                        showJobDetails(currentJobs);
                    } else {
                        System.out.println("❌ No jobs loaded. Please scrape first (Option 1 or 2)");
                    }
                }
                case "4" -> {
                    if (!currentJobs.isEmpty()) {
                        System.out.print("Enter keyword to filter: ");
                        String keyword = scanner.nextLine().trim();
                        List<JobListing> filtered = filterJobsByKeyword(currentJobs, keyword);
                        System.out.println("\n🔍 Found " + filtered.size() + " jobs matching '" + keyword + "'");
                        displayJobsMenu(filtered);
                        showJobDetails(filtered);
                    } else {
                        System.out.println("❌ No jobs loaded. Please scrape first");
                    }
                }
                case "5" -> {
                    if (!currentJobs.isEmpty()) {
                        System.out.print("Enter location to filter (e.g., Remote, New York): ");
                        String location = scanner.nextLine().trim();
                        List<JobListing> filtered = filterJobsByLocation(currentJobs, location);
                        System.out.println("\n📍 Found " + filtered.size() + " jobs in '" + location + "'");
                        displayJobsMenu(filtered);
                        showJobDetails(filtered);
                    } else {
                        System.out.println("❌ No jobs loaded. Please scrape first");
                    }
                }
                case "6" -> {
                    if (!currentJobs.isEmpty()) {
                        generateStatisticsReport(currentJobs);
                    } else {
                        System.out.println("❌ No jobs loaded. Please scrape first");
                    }
                }
                case "7" -> {
                    if (!currentJobs.isEmpty()) {
                        saveJobsToCsv(currentJobs, "jobs_export.csv");
                    } else {
                        System.out.println("❌ No jobs to save");
                    }
                }
                case "8" -> {
                    if (!currentJobs.isEmpty()) {
                        saveJobsToJson(currentJobs, "jobs_export.json");
                    } else {
                        System.out.println("❌ No jobs to save");
                    }
                }
                case "9" -> testWithDifferentSites();
                case "10" -> {
                    System.out.println("\n👋 Thank you for using Job Listing Scraper!");
                    System.out.println("💼 Good luck with your job search!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Please enter 1-10");
            }
            
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    // ==============================================
    // MAIN FUNCTION
    // ==============================================
    
    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🌟 WELCOME TO JOB LISTING WEB SCRAPER 🌟");
        System.out.println("=".repeat(60));
        System.out.println("\n📌 This tool scrapes job listings from various websites");
        System.out.println("📌 Features:");
        System.out.println("   • Scrape jobs from RemoteOK.io and Indeed (demo)");
        System.out.println("   • Filter by keyword and location");
        System.out.println("   • Save to CSV/JSON files");
        System.out.println("   • Generate statistics report");
        System.out.println("\n⚠️  Note: For learning purposes only");
        System.out.println("⚠️  Always respect websites' terms of service");
        System.out.println("⚠️  Real production scraping requires more advanced techniques");
        
        interactiveScraper();
    }
}