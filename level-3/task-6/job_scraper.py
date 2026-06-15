"""
==================================================
JOB LISTING WEB SCRAPER - LEVEL 3 TASK 6
Cognifyz Internship Program
==================================================
Features: Scrape job listings, filter by keyword,
          save to CSV, user-friendly display
==================================================
"""

import requests
from bs4 import BeautifulSoup
import time
import csv
import json
import os
from datetime import datetime

# ==============================================
# CONFIGURATION
# ==============================================

# User-Agent to identify our scraper (be polite!)
HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
}

# Rate limiting (be gentle to servers)
REQUEST_DELAY = 2  # seconds between requests

# ==============================================
# STEP 1: SELECT WEBSITE AND IDENTIFY DATA
# ==============================================

"""
For learning purposes, we'll scrape from:
- RemoteOK.io (public API-like structure)
- We'll also provide a demo for Indeed-style structure

Data to extract:
- Job Title
- Company Name
- Location
- Salary (if available)
- Job Description (summary)
- Job URL
- Posted Date
"""

class JobListing:
    """Class to store job listing data"""
    
    def __init__(self, title, company, location, salary, description, url, posted_date):
        self.title = title
        self.company = company
        self.location = location
        self.salary = salary
        self.description = description
        self.url = url
        self.posted_date = posted_date
    
    def display_short(self):
        """Display short version for listing"""
        print(f"\n{'='*60}")
        print(f"📌 {self.title}")
        print(f"🏢 {self.company} | 📍 {self.location}")
        if self.salary:
            print(f"💰 {self.salary}")
        print(f"📅 Posted: {self.posted_date}")
        print(f"🔗 {self.url[:80]}...")
        print(f"📝 {self.description[:150]}...")
        print(f"{'='*60}")
    
    def to_dict(self):
        """Convert to dictionary for JSON/CSV"""
        return {
            'title': self.title,
            'company': self.company,
            'location': self.location,
            'salary': self.salary,
            'description': self.description,
            'url': self.url,
            'posted_date': self.posted_date,
            'scraped_date': datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        }


# ==============================================
# DEMO SCRAPER (RemoteOK.io - Public API)
# ==============================================

def scrape_remoteok_jobs(job_title=""):
    """
    Scrape jobs from RemoteOK.io
    This site has a simple structure and is scraper-friendly
    """
    print("\n🌐 Scraping RemoteOK.io for jobs...")
    
    # Build URL with search query
    if job_title:
        url = f"https://remoteok.io/remote-dev+{job_title.replace(' ', '-')}-jobs"
    else:
        url = "https://remoteok.io/remote-dev-jobs"
    
    try:
        response = requests.get(url, headers=HEADERS)
        response.raise_for_status()
        
        soup = BeautifulSoup(response.content, 'html.parser')
        jobs = []
        
        # Find job postings
        job_cards = soup.find_all('tr', class_='job')
        
        for card in job_cards[:20]:  # Limit to 20 jobs for demo
            try:
                # Extract job title
                title_elem = card.find('h2', itemprop='title')
                title = title_elem.text.strip() if title_elem else "N/A"
                
                # Extract company
                company_elem = card.find('h3', itemprop='name')
                company = company_elem.text.strip() if company_elem else "N/A"
                
                # Extract location (RemoteOK is all remote)
                location = "Remote (Worldwide)"
                
                # Extract salary info
                salary_elem = card.find('div', class_='location')
                salary = salary_elem.text.strip() if salary_elem else "Not specified"
                
                # Extract description preview
                desc_elem = card.find('div', class_='description')
                description = desc_elem.text.strip()[:300] if desc_elem else "No description"
                
                # Extract URL
                link_elem = card.find('a', class_='preventLink')
                url_suffix = link_elem.get('href') if link_elem else ""
                job_url = f"https://remoteok.io{url_suffix}" if url_suffix else "N/A"
                
                # Extract posted date
                date_elem = card.find('td', class_='time')
                posted_date = date_elem.text.strip() if date_elem else "Recently"
                
                job = JobListing(
                    title, company, location, salary,
                    description, job_url, posted_date
                )
                jobs.append(job)
                
            except Exception as e:
                print(f"⚠️ Error parsing job card: {e}")
                continue
        
        print(f"✅ Found {len(jobs)} jobs")
        return jobs
        
    except requests.RequestException as e:
        print(f"❌ Error fetching jobs: {e}")
        return []


# ==============================================
# DEMO SCRAPER 2: Mock Indeed-like Scraper
# ==============================================

class DemoJobScraper:
    """
    Demo scraper that simulates Indeed-style scraping
    Since real Indeed blocks scrapers, we use sample data
    But the structure shows how you would do it
    """
    
    @staticmethod
    def get_sample_jobs():
        """Return sample job data for demonstration"""
        sample_jobs = [
            JobListing(
                "Senior Python Developer",
                "TechCorp Solutions",
                "New York, NY (Remote)",
                "$120,000 - $150,000",
                "Looking for an experienced Python developer with 5+ years experience in Django and FastAPI. Must have strong database skills.",
                "https://example.com/job/1",
                "2 days ago"
            ),
            JobListing(
                "Data Scientist",
                "Analytics Pro",
                "San Francisco, CA",
                "$130,000 - $160,000",
                "Seeking data scientist with ML experience. Python, TensorFlow, and SQL required.",
                "https://example.com/job/2",
                "1 day ago"
            ),
            JobListing(
                "Frontend React Developer",
                "Web Innovations",
                "Austin, TX (Hybrid)",
                "$100,000 - $130,000",
                "React, TypeScript, and Next.js expert needed for exciting web projects.",
                "https://example.com/job/3",
                "3 days ago"
            ),
            JobListing(
                "DevOps Engineer",
                "Cloud Systems Inc",
                "Seattle, WA",
                "$115,000 - $145,000",
                "AWS, Docker, Kubernetes experience required. CI/CD pipeline management.",
                "https://example.com/job/4",
                "Yesterday"
            ),
            JobListing(
                "Full Stack JavaScript Developer",
                "Startup Hub",
                "Remote (US only)",
                "$90,000 - $120,000",
                "Node.js, React, MongoDB. Fast-paced startup environment.",
                "https://example.com/job/5",
                "5 days ago"
            ),
        ]
        return sample_jobs
    
    @staticmethod
    def scrape_indeed(job_title="python", location=""):
        """
        Demo of how you would scrape Indeed
        Note: Real Indeed scraping requires more sophisticated methods
        """
        print(f"\n🔍 Simulating Indeed scrape for '{job_title}'...")
        print("💡 Note: Using sample data for demonstration")
        print("   Real implementation would require rotating proxies, cookies, etc.\n")
        
        return DemoJobScraper.get_sample_jobs()


# ==============================================
# STEP 2: UTILIZE WEB SCRAPING LIBRARY
# ==============================================

def scrape_with_retry(url, max_retries=3):
    """
    Scrape with retry logic for robustness
    """
    for attempt in range(max_retries):
        try:
            response = requests.get(url, headers=HEADERS, timeout=10)
            response.raise_for_status()
            return response
        except requests.RequestException as e:
            print(f"⚠️ Attempt {attempt + 1} failed: {e}")
            if attempt < max_retries - 1:
                time.sleep(REQUEST_DELAY)
            else:
                raise
    return None


def scrape_jobs_from_multiple_sources(job_title=""):
    """
    Combine jobs from multiple sources
    """
    all_jobs = []
    
    # Source 1: RemoteOK
    print("\n📡 SOURCE 1: RemoteOK.io")
    remote_jobs = scrape_remoteok_jobs(job_title)
    all_jobs.extend(remote_jobs)
    
    # Delay between sources (be polite!)
    time.sleep(REQUEST_DELAY)
    
    # Source 2: Demo Indeed scraper
    print("\n📡 SOURCE 2: Indeed (Demo)")
    indeed_scraper = DemoJobScraper()
    indeed_jobs = indeed_scraper.scrape_indeed(job_title)
    all_jobs.extend(indeed_jobs)
    
    return all_jobs


# ==============================================
# STEP 3: DESIGN USER-FRIENDLY PRESENTATION
# ==============================================

def display_jobs_menu(jobs):
    """
    Display jobs in a user-friendly format
    """
    if not jobs:
        print("\n❌ No jobs found!")
        return False
    
    print("\n" + "="*70)
    print(f"📊 JOB LISTINGS SUMMARY ({len(jobs)} jobs found)")
    print("="*70)
    
    # Display jobs with numbers
    for idx, job in enumerate(jobs, 1):
        print(f"\n{idx}. {job.title}")
        print(f"   🏢 {job.company} | 📍 {job.location}")
        if job.salary and job.salary != "Not specified":
            print(f"   💰 {job.salary}")
        print(f"   📅 Posted: {job.posted_date}")
    
    return True


def show_job_details(jobs):
    """
    Show detailed view of selected job
    """
    if not jobs:
        return
    
    while True:
        try:
            choice = input(f"\n🔍 Enter job number to view details (1-{len(jobs)}) or 0 to go back: ")
            if choice == '0':
                break
            
            idx = int(choice) - 1
            if 0 <= idx < len(jobs):
                jobs[idx].display_short()
            else:
                print(f"❌ Invalid choice. Please enter 1-{len(jobs)}")
        except ValueError:
            print("❌ Please enter a valid number")


def filter_jobs_by_keyword(jobs, keyword):
    """
    Filter jobs by keyword in title or description
    """
    keyword_lower = keyword.lower()
    filtered = []
    
    for job in jobs:
        if (keyword_lower in job.title.lower() or 
            keyword_lower in job.description.lower()):
            filtered.append(job)
    
    return filtered


def filter_jobs_by_location(jobs, location):
    """
    Filter jobs by location
    """
    location_lower = location.lower()
    filtered = []
    
    for job in jobs:
        if location_lower in job.location.lower():
            filtered.append(job)
    
    return filtered


def save_jobs_to_csv(jobs, filename="jobs_export.csv"):
    """
    Save job listings to CSV file
    """
    if not jobs:
        print("❌ No jobs to save")
        return False
    
    try:
        with open(filename, 'w', newline='', encoding='utf-8') as file:
            writer = csv.DictWriter(file, fieldnames=jobs[0].to_dict().keys())
            writer.writeheader()
            for job in jobs:
                writer.writerow(job.to_dict())
        
        print(f"✅ Saved {len(jobs)} jobs to '{filename}'")
        return True
    except Exception as e:
        print(f"❌ Error saving to CSV: {e}")
        return False


def save_jobs_to_json(jobs, filename="jobs_export.json"):
    """
    Save job listings to JSON file
    """
    if not jobs:
        print("❌ No jobs to save")
        return False
    
    try:
        jobs_data = [job.to_dict() for job in jobs]
        with open(filename, 'w', encoding='utf-8') as file:
            json.dump(jobs_data, file, indent=2)
        
        print(f"✅ Saved {len(jobs)} jobs to '{filename}'")
        return True
    except Exception as e:
        print(f"❌ Error saving to JSON: {e}")
        return False


def generate_statistics_report(jobs):
    """
    Generate statistics about scraped jobs
    """
    if not jobs:
        print("❌ No data to generate statistics")
        return
    
    print("\n" + "="*60)
    print("📊 JOB MARKET STATISTICS REPORT")
    print("="*60)
    
    # Job titles analysis
    print(f"\n📌 Total Jobs Found: {len(jobs)}")
    
    # Companies count
    companies = {}
    locations = {}
    
    for job in jobs:
        companies[job.company] = companies.get(job.company, 0) + 1
        city = job.location.split(',')[0] if ',' in job.location else job.location
        locations[city] = locations.get(city, 0) + 1
    
    print(f"\n🏢 Top Companies Hiring:")
    for company, count in sorted(companies.items(), key=lambda x: x[1], reverse=True)[:5]:
        print(f"   • {company}: {count} job(s)")
    
    print(f"\n📍 Top Locations:")
    for location, count in sorted(locations.items(), key=lambda x: x[1], reverse=True)[:5]:
        print(f"   • {location}: {count} job(s)")
    
    # Salary analysis (if available)
    salaries = []
    for job in jobs:
        if job.salary and job.salary != "Not specified":
            # Simple salary parsing
            import re
            numbers = re.findall(r'\d+', job.salary)
            if numbers:
                salaries.append(int(numbers[0]) * 1000 if len(numbers[0]) == 2 else int(numbers[0]))
    
    if salaries:
        print(f"\n💰 Average Salary: ${sum(salaries)//len(salaries):,}")
        print(f"   Salary Range: ${min(salaries):,} - ${max(salaries):,}")
    
    print("="*60)


# ==============================================
# STEP 4: TEST WITH DIFFERENT WEBSITES
# ==============================================

def test_with_different_sites():
    """
    Test the scraper with different configurations
    """
    print("\n" + "="*60)
    print("🧪 TESTING SCRAPER WITH DIFFERENT CONFIGURATIONS")
    print("="*60)
    
    test_cases = [
        {"name": "All Jobs", "job_title": "", "location": ""},
        {"name": "Python Jobs", "job_title": "python", "location": ""},
        {"name": "JavaScript Jobs", "job_title": "javascript", "location": ""},
        {"name": "Remote Jobs", "job_title": "", "location": "remote"},
    ]
    
    for test in test_cases:
        print(f"\n📋 TEST: {test['name']}")
        print("-" * 40)
        
        jobs = scrape_jobs_from_multiple_sources(test['job_title'])
        print(f"✅ Results: {len(jobs)} jobs found")
        
        # Small delay between tests
        time.sleep(REQUEST_DELAY)


def interactive_scraper():
    """
    Main interactive menu for the job scraper
    """
    current_jobs = []
    
    while True:
        print("\n" + "="*60)
        print("💼 JOB LISTING WEB SCRAPER 💼")
        print("="*60)
        print("1. 🔍 Scrape Jobs (Default)")
        print("2. 🔎 Scrape Jobs with Keyword (e.g., Python)")
        print("3. 📋 View All Jobs")
        print("4. 🔍 Filter by Keyword (in title/description)")
        print("5. 📍 Filter by Location")
        print("6. 📊 View Statistics Report")
        print("7. 💾 Save Jobs to CSV")
        print("8. 💾 Save Jobs to JSON")
        print("9. 🧪 Run Test with Different Websites")
        print("10. ❌ Exit")
        print("="*60)
        
        choice = input("\nEnter your choice (1-10): ")
        
        if choice == '1':
            print("\n🔄 Scraping jobs... (This may take a few seconds)")
            current_jobs = scrape_jobs_from_multiple_sources()
            display_jobs_menu(current_jobs)
            
        elif choice == '2':
            keyword = input("Enter job keyword (e.g., Python, JavaScript): ")
            print(f"\n🔄 Scraping jobs for '{keyword}'...")
            current_jobs = scrape_jobs_from_multiple_sources(keyword)
            display_jobs_menu(current_jobs)
            
        elif choice == '3':
            if current_jobs:
                display_jobs_menu(current_jobs)
                show_job_details(current_jobs)
            else:
                print("❌ No jobs loaded. Please scrape first (Option 1 or 2)")
        
        elif choice == '4':
            if current_jobs:
                keyword = input("Enter keyword to filter: ")
                filtered = filter_jobs_by_keyword(current_jobs, keyword)
                print(f"\n🔍 Found {len(filtered)} jobs matching '{keyword}'")
                display_jobs_menu(filtered)
                show_job_details(filtered)
            else:
                print("❌ No jobs loaded. Please scrape first")
        
        elif choice == '5':
            if current_jobs:
                location = input("Enter location to filter (e.g., Remote, New York): ")
                filtered = filter_jobs_by_location(current_jobs, location)
                print(f"\n📍 Found {len(filtered)} jobs in '{location}'")
                display_jobs_menu(filtered)
                show_job_details(filtered)
            else:
                print("❌ No jobs loaded. Please scrape first")
        
        elif choice == '6':
            if current_jobs:
                generate_statistics_report(current_jobs)
            else:
                print("❌ No jobs loaded. Please scrape first")
        
        elif choice == '7':
            if current_jobs:
                save_jobs_to_csv(current_jobs)
            else:
                print("❌ No jobs to save")
        
        elif choice == '8':
            if current_jobs:
                save_jobs_to_json(current_jobs)
            else:
                print("❌ No jobs to save")
        
        elif choice == '9':
            test_with_different_sites()
        
        elif choice == '10':
            print("\n👋 Thank you for using Job Listing Scraper!")
            print("💼 Good luck with your job search!")
            break
        
        else:
            print("❌ Invalid choice. Please enter 1-10")
        
        input("\nPress Enter to continue...")


# ==============================================
# MAIN FUNCTION
# ==============================================

def main():
    """
    Main function - Entry point of the program
    """
    print("\n" + "="*60)
    print("🌟 WELCOME TO JOB LISTING WEB SCRAPER 🌟")
    print("="*60)
    print("\n📌 This tool scrapes job listings from various websites")
    print("📌 Features:")
    print("   • Scrape jobs from RemoteOK.io and Indeed (demo)")
    print("   • Filter by keyword and location")
    print("   • Save to CSV/JSON files")
    print("   • Generate statistics report")
    print("\n⚠️  Note: For learning purposes only")
    print("⚠️  Always respect websites' terms of service")
    print("⚠️  Real production scraping requires more advanced techniques")
    
    # Start interactive scraper
    interactive_scraper()


# ==============================================
# RUN THE PROGRAM
# ==============================================

if __name__ == "__main__":
    # Install required libraries if not present
    try:
        import requests
        from bs4 import BeautifulSoup
    except ImportError:
        print("❌ Required libraries not found!")
        print("\n📦 Please install required libraries:")
        print("   pip install requests beautifulsoup4")
        print("\nThen run the program again.")
        exit(1)
    
    main()