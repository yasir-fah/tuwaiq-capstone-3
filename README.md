# StartHub

**Version:** 1.0  
**Developers:** Essa Al Ghanim - Faisal Al Khrayef - Yasir Alateeq

---

## Endpoints I made:
- `- getAllAdvisorSessionsByStartup 
` – Get adivsor session by specific startup-
- `- addAdvisorSessionByStartup 
` – add new session by startup.
- `- advisorAcceptAdvisorSession 
` – allow advisor to accept the session.
- `- advisorRejectAdvisorSession 
` – allow advisor to reject the session.
- `- startupCancelAdvisorRequest 
` – Allow the startup to cancel the session requst.
- `- updateAdvisorSession 
` – Update the session by startup .
- `- deleteAdvisorSession 
` – Delete advisor session by startup.
- `- getAllFreelancerProjectByStatus 
` – Get all project of freelancer depend on it status.
- `- getAllFreelancerProject 
` – Get project of one startup.
- `- addFreelancerProjectByStartup 
` – allow startup to add project.
- `- freelancerAcceptFreelancerProject 
` – allow freelancer accept the project.- 
- ` - freelancerRejectFreelancerProject
` – allow freelancer reject the project.- 
- ` - startupCancelFreelanceRequest
` – allow freelancer Cancel the project.- 
- ` - updateFreelancerProject
` – allow startup to update the project requirement.-
- ` - deleteFreelancerProject
` – allow startup to delete the project.


---

## External Api's & Tools I fetched:
- `S3Bucket` – Amazon S3 (Simple Storage Service) is used to store and retrieve files (objects) in the cloud. It eliminates the need for local file storage and is commonly used for hosting static assets, backups, and large-scale data storage.(used for upload contract file)  

- `RDS` – Amazon Relational Database Service provides a fully managed relational database in the cloud. It supports engines like MySQL, PostgreSQL, Oracle, and SQL Server. AWS takes care of backups, patching, and scaling, making it reliable and easy to use.  

- `ElasticBeanstalk` – AWS Elastic Beanstalk is a PaaS (Platform as a Service) that allows you to deploy and manage applications without worrying about infrastructure.

- `JavaMailSender` – A Spring Boot utility that simplifies sending emails from the application.
  
---

## Overview
StartHub is a digital platform designed to empower Saudi entrepreneurs and early-stage startups. It provides structured guidance, advisory services, team-building opportunities, and growth support — all in one place.

---

## Goals
- Validate business ideas quickly with clear guidance.
- Provide access to legal, advisory, and market services.
- Help startups build effective teams (founders, freelancers, advisors, investors).

---

## Features
- **Consultancy:**
    - Book human advisors for expert sessions.
    - Access an AI-powered advisor for instant guidance.

- **Meetings & Sessions:**
    - Schedule and manage advisor meetings.
    - Get meeting summaries, bullet-point notes, action items, and audio recordings.
    - Add sessions to calendars.

- **Team Building:**
    - Connect with potential co-founders based on goals and skills.
    - Hire freelancers for specific tasks.

- **Growth Support:**
    - Access investors.
    - Manage and track investments.
    - Create and view contracts.

---

## Tech Stack
- **Backend:** Spring Boot (Java 17)
- **Database:** MySQL (AWS RDS)
- **ORM:** Hibernate with JPA
- **Deployment:** AWS (Elastic Beanstalk, ECS)
- **CI/CD:** GitHub

---
## Database diagram
<img width="536" height="872" alt="image" src="https://github.com/user-attachments/assets/7ccbb296-2d5e-47c3-b284-b4d4906c614a" />


---
## presentation file
📄 [Capstone Presentation (PDF)](./Capstone-3.pdf)
