import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
    """Set cell padding in twips."""
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    tcMar = OxmlElement('w:tcMar')
    for m, val in [('top', top), ('bottom', bottom), ('left', left), ('right', right)]:
        node = OxmlElement(f'w:{m}')
        node.set(qn('w:w'), str(val))
        node.set(qn('w:type'), 'dxa')
        tcMar.append(node)
    tcPr.append(tcMar)

def set_cell_shading(cell, color_hex):
    """Set background color of a table cell."""
    shading_elm = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{color_hex}"/>')
    cell._tc.get_or_add_tcPr().append(shading_elm)

def add_code_block(doc, code_text):
    """Add a styled code block with Courier New font."""
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(4)
    p.paragraph_format.space_after = Pt(8)
    p.paragraph_format.line_spacing = 1.15
    run = p.add_run(code_text)
    run.font.name = 'Courier New'
    run.font.size = Pt(8.5)
    run.font.color.rgb = RGBColor(30, 41, 59)

def add_screenshot_placeholder(doc, fig_title, instructions):
    """Create a formatted placeholder box for pasting screenshots."""
    p_title = doc.add_paragraph()
    p_title.paragraph_format.space_before = Pt(10)
    p_title.paragraph_format.space_after = Pt(4)
    run_title = p_title.add_run(fig_title)
    run_title.font.name = 'Calibri'
    run_title.font.size = Pt(11)
    run_title.font.bold = True
    run_title.font.color.rgb = RGBColor(30, 58, 138) # Dark blue

    # Table acting as a visual paste box
    table = doc.add_table(rows=1, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    
    cell = table.cell(0, 0)
    cell.width = Inches(6.5)
    set_cell_shading(cell, 'F8FAFC')
    set_cell_margins(cell, top=200, bottom=200, left=200, right=200)

    # Dashed/light border
    tcPr = cell._tc.get_or_add_tcPr()
    tcBorders = parse_xml(
        f'<w:tcBorders {nsdecls("w")}>'
        '<w:top w:val="dashed" w:sz="6" w:space="0" w:color="94A3B8"/>'
        '<w:left w:val="dashed" w:sz="6" w:space="0" w:color="94A3B8"/>'
        '<w:bottom w:val="dashed" w:sz="6" w:space="0" w:color="94A3B8"/>'
        '<w:right w:val="dashed" w:sz="6" w:space="0" w:color="94A3B8"/>'
        '</w:tcBorders>'
    )
    tcPr.append(tcBorders)

    cp = cell.paragraphs[0]
    cp.alignment = WD_ALIGN_PARAGRAPH.CENTER
    c_run1 = cp.add_run("📷 [ PASTE SCREENSHOT HERE ]\n")
    c_run1.font.bold = True
    c_run1.font.size = Pt(10)
    c_run1.font.color.rgb = RGBColor(79, 70, 229)

    c_run2 = cp.add_run(instructions)
    c_run2.font.size = Pt(9)
    c_run2.font.italic = True
    c_run2.font.color.rgb = RGBColor(100, 116, 139)

    # Trailing space
    p_sp = doc.add_paragraph()
    p_sp.paragraph_format.space_before = Pt(2)
    p_sp.paragraph_format.space_after = Pt(6)

def build_document():
    doc = docx.Document()

    # Set page margins to 0.75 in
    for s in doc.sections:
        s.top_margin = Inches(0.75)
        s.bottom_margin = Inches(0.75)
        s.left_margin = Inches(0.75)
        s.right_margin = Inches(0.75)

    # Document Header
    p_h1 = doc.add_paragraph()
    p_h1.paragraph_format.space_after = Pt(2)
    r_h1 = p_h1.add_run("OOPJ (CS336B)")
    r_h1.font.name = 'Calibri'
    r_h1.font.size = Pt(20)
    r_h1.font.bold = True
    r_h1.font.color.rgb = RGBColor(15, 23, 42)

    p_h2 = doc.add_paragraph()
    p_h2.paragraph_format.space_after = Pt(14)
    r_h2 = p_h2.add_run("Moodle MSE2 Submission Format")
    r_h2.font.name = 'Calibri'
    r_h2.font.size = Pt(16)
    r_h2.font.bold = True
    r_h2.font.color.rgb = RGBColor(79, 70, 229)

    # Table 0: Student Details
    t0_data = [
        ("Name", "[FILL: YOUR FULL NAME - e.g. Harsh / Shashwat]"),
        ("Branch", "Computer Science & Engineering (CSE)"),
        ("Roll Number", "[FILL: YOUR 15-DIGIT UNIVERSITY ROLL NUMBER]"),
        ("Section", "[FILL: YOUR SECTION - e.g. CSE-B]"),
        ("Shift", "[FILL: YOUR SHIFT - e.g. Morning / Evening]"),
        ("Case Study Name", "Library Management REST API (Spring Boot + Spring JDBC + MySQL)")
    ]

    t0 = doc.add_table(rows=len(t0_data), cols=2)
    t0.style = 'Table Grid'
    t0.alignment = WD_TABLE_ALIGNMENT.CENTER
    t0.autofit = False

    for i, (label, val) in enumerate(t0_data):
        c0 = t0.cell(i, 0)
        c1 = t0.cell(i, 1)
        c0.width = Inches(2.2)
        c1.width = Inches(4.5)
        set_cell_margins(c0, 100, 100, 150, 150)
        set_cell_margins(c1, 100, 100, 150, 150)
        set_cell_shading(c0, 'F1F5F9')

        p0 = c0.paragraphs[0]
        r0 = p0.add_run(label)
        r0.font.bold = True
        r0.font.size = Pt(10)

        p1 = c1.paragraphs[0]
        r1 = p1.add_run(val)
        r1.font.size = Pt(10)
        if "[FILL:" in val:
            r1.font.color.rgb = RGBColor(220, 38, 38)
            r1.font.bold = True
        else:
            r1.font.color.rgb = RGBColor(15, 23, 42)

    doc.add_paragraph().paragraph_format.space_after = Pt(10)

    # ========================================================================
    # QUESTION 1 SECTION
    # ========================================================================
    p_q1 = doc.add_paragraph()
    p_q1.paragraph_format.space_before = Pt(12)
    p_q1.paragraph_format.space_after = Pt(4)
    rq1 = p_q1.add_run("Q1.")
    rq1.font.size = Pt(16)
    rq1.font.bold = True
    rq1.font.color.rgb = RGBColor(15, 23, 42)

    p_link_h = doc.add_paragraph()
    p_link_h.paragraph_format.space_after = Pt(4)
    r_lh = p_link_h.add_run("Moodle Upload/GitHub Repository Link")
    r_lh.font.size = Pt(14)
    r_lh.font.bold = True

    t_link = doc.add_table(rows=2, cols=1)
    t_link.style = 'Table Grid'
    t_link.alignment = WD_TABLE_ALIGNMENT.CENTER
    t_link.autofit = False

    cell_l0 = t_link.cell(0, 0)
    cell_l0.width = Inches(6.7)
    set_cell_margins(cell_l0, 120, 120, 150, 150)
    p_l0 = cell_l0.paragraphs[0]
    r_l0 = p_l0.add_run("GitHub Repository: [FILL: Paste your GitHub repository link here, or 'N/A — Project submitted as ZIP file on Moodle LMS']\n"
                        "Moodle Upload: OOPJ_MSE2_Project_LibraryManagement_[YourName].zip")
    r_l0.font.size = Pt(9.5)

    cell_l1 = t_link.cell(1, 0)
    cell_l1.width = Inches(6.7)
    set_cell_margins(cell_l1, 80, 80, 150, 150)
    cell_l1.paragraphs[0].add_run("Submission Status: Complete Spring Boot CRUD Project ready for LMS Upload").font.size = Pt(9)

    doc.add_paragraph().paragraph_format.space_after = Pt(8)

    # Project Code Header
    p_code_h = doc.add_paragraph()
    p_code_h.paragraph_format.space_before = Pt(10)
    p_code_h.paragraph_format.space_after = Pt(4)
    r_ch = p_code_h.add_run("Project code")
    r_ch.font.size = Pt(14)
    r_ch.font.bold = True

    # Read project source files
    with open(r'c:\Users\harsh\eclipse-workspace\Shashwat\src\main\java\com\kiet\examproject\model\Book.java', 'r', encoding='utf-8') as f:
        code_book = f.read()
    with open(r'c:\Users\harsh\eclipse-workspace\Shashwat\src\main\java\com\kiet\examproject\dao\BookDao.java', 'r', encoding='utf-8') as f:
        code_dao = f.read()
    with open(r'c:\Users\harsh\eclipse-workspace\Shashwat\src\main\java\com\kiet\examproject\service\BookService.java', 'r', encoding='utf-8') as f:
        code_service = f.read()
    with open(r'c:\Users\harsh\eclipse-workspace\Shashwat\src\main\java\com\kiet\examproject\controller\BookController.java', 'r', encoding='utf-8') as f:
        code_controller = f.read()
    with open(r'c:\Users\harsh\eclipse-workspace\Shashwat\src\main\java\com\kiet\examproject\config\AppConfig.java', 'r', encoding='utf-8') as f:
        code_config = f.read()
    with open(r'c:\Users\harsh\eclipse-workspace\Shashwat\src\main\java\com\kiet\examproject\aspect\LoggingAspect.java', 'r', encoding='utf-8') as f:
        code_aspect = f.read()
    with open(r'c:\Users\harsh\eclipse-workspace\Shashwat\src\main\resources\schema.sql', 'r', encoding='utf-8') as f:
        code_schema = f.read()
    with open(r'c:\Users\harsh\eclipse-workspace\Shashwat\src\main\resources\application.properties', 'r', encoding='utf-8') as f:
        code_props = f.read()

    combined_code = (
        "// ==================================================================\n"
        "// FILE 1: model/Book.java (Domain Entity)\n"
        "// ==================================================================\n" + code_book + "\n\n"
        "// ==================================================================\n"
        "// FILE 2: dao/BookDao.java (Spring JdbcTemplate CRUD + Raw JDBC Demo)\n"
        "// ==================================================================\n" + code_dao + "\n\n"
        "// ==================================================================\n"
        "// FILE 3: service/BookService.java (Business Logic + DI + Lifecycle)\n"
        "// ==================================================================\n" + code_service + "\n\n"
        "// ==================================================================\n"
        "// FILE 4: controller/BookController.java (REST Endpoints)\n"
        "// ==================================================================\n" + code_controller + "\n\n"
        "// ==================================================================\n"
        "// FILE 5: config/AppConfig.java (Spring Bean Configuration & Prototype Scope)\n"
        "// ==================================================================\n" + code_config + "\n\n"
        "// ==================================================================\n"
        "// FILE 6: aspect/LoggingAspect.java (Spring AOP Logging)\n"
        "// ==================================================================\n" + code_aspect + "\n\n"
        "// ==================================================================\n"
        "// FILE 7: schema.sql (MySQL Database DDL Initialization)\n"
        "// ==================================================================\n" + code_schema + "\n\n"
        "// ==================================================================\n"
        "// FILE 8: application.properties (Application & Datasource Configuration)\n"
        "// ==================================================================\n" + code_props
    )
    add_code_block(doc, combined_code)

    # Screenshots Section Header
    p_ss_h = doc.add_paragraph()
    p_ss_h.paragraph_format.space_before = Pt(14)
    p_ss_h.paragraph_format.space_after = Pt(4)
    r_ssh = p_ss_h.add_run("Screenshots of Model, Controller, and Postman")
    r_ssh.font.size = Pt(14)
    r_ssh.font.bold = True

    # Screenshot Placeholders with Explicit Headings
    add_screenshot_placeholder(
        doc,
        "Fig 1: Book.java – Model Class (POJO with Fields, Constructors, Getters & Setters)",
        "Instructions: Open 'Book.java' in Eclipse IDE or VS Code and take a screenshot showing the class declaration, private fields, constructors, and getters/setters."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 2: BookController.java – REST Controller Implementation (@RestController, @RequestMapping)",
        "Instructions: Open 'BookController.java' in Eclipse IDE showing the mapping annotations (@GetMapping, @PostMapping, @PutMapping, @DeleteMapping)."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 3: Postman / Browser – POST /api/books (201 Created – Add New Book)",
        "Instructions: Send a POST request to 'http://localhost:8080/api/books' with JSON body: {\"title\":\"Clean Code\",\"author\":\"Robert C. Martin\",\"category\":\"Software Engineering\",\"availableCopies\":10}. Capture response 201 Created."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 4: Postman / Browser – GET /api/books (200 OK – Retrieve All Books)",
        "Instructions: Send a GET request to 'http://localhost:8080/api/books'. Capture the 200 OK status and the JSON array containing all book records retrieved from MySQL."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 5: Postman / Browser – GET /api/books/{id} (200 OK – Retrieve Book by ID)",
        "Instructions: Send a GET request to 'http://localhost:8080/api/books/1'. Capture the single book JSON object returned."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 6: Postman / Browser – PUT /api/books/{id} (200 OK – Update Book Details)",
        "Instructions: Send a PUT request to 'http://localhost:8080/api/books/1' with updated JSON (title, author, category, availableCopies). Capture the 200 OK response."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 7: Postman / Browser – DELETE /api/books/{id} (200 OK & 404 Not Found Handling)",
        "Instructions: Send a DELETE request to 'http://localhost:8080/api/books/1'. Capture the success message, then send DELETE /api/books/999 to show the 404 Not Found response."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 8: Postman / Browser – GET /api/books/raw/{id} (Raw JDBC Demonstration Output)",
        "Instructions: Send a GET request to 'http://localhost:8080/api/books/raw/1' (or click 'Verify via Raw JDBC' on the Admin Dashboard) to capture the raw DriverManager query execution."
    )

    # Project Structure Header
    p_struct_h = doc.add_paragraph()
    p_struct_h.paragraph_format.space_before = Pt(14)
    p_struct_h.paragraph_format.space_after = Pt(4)
    r_sth = p_struct_h.add_run("Screenshot of Eclipse/Intellij Project Structure.")
    r_sth.font.size = Pt(14)
    r_sth.font.bold = True

    add_screenshot_placeholder(
        doc,
        "Fig 9: Eclipse IDE Project Structure – Package Explorer (com.kiet.examproject)",
        "Instructions: Expand the 'demo' or 'Shashwat' project in Eclipse Package Explorer. Show 'src/main/java' with all packages (model, dao, service, controller, config, aspect), 'src/main/resources' (application.properties, schema.sql, static/index.html), and pom.xml."
    )

    # AI Prompts Header
    p_prompts_h = doc.add_paragraph()
    p_prompts_h.paragraph_format.space_before = Pt(14)
    p_prompts_h.paragraph_format.space_after = Pt(4)
    r_prh = p_prompts_h.add_run("Write three AI Prompts used for creating the Project.")
    r_prh.font.size = Pt(14)
    r_prh.font.bold = True

    prompts_data = [
        (
            "Prompt 1:\n\n"
            "Act as an experienced Java Spring Boot developer. Create a Library Management REST API for a college library to manage book records using Spring Boot, Spring JDBC (JdbcTemplate), and MySQL. The entity Book should have fields: id (Integer, auto-increment), title (String), author (String), category (String), availableCopies (Integer). Implement a complete MVC architecture with Model, DAO, Service, and Controller layers. Include REST endpoints for POST /api/books (create book), GET /api/books (fetch all), GET /api/books/{id} (fetch by id), PUT /api/books/{id} (update details), and DELETE /api/books/{id} (delete with appropriate HTTP responses). Do not use JPA, Hibernate, or ORM frameworks.",
            
            "Reflection 1:\n\n"
            "This prompt established the complete architectural backbone of the application following strict MVC separation of concerns. I learned how Spring's JdbcTemplate eliminates ORM overhead while abstracting JDBC boilerplate. I understood how RowMapper lambda converts MySQL result rows into Java Book objects, how GeneratedKeyHolder retrieves auto-incremented primary keys upon INSERT, and how parameterized SQL prevents SQL injection vulnerabilities. I also learned how the layers interact seamlessly: Controller → Service → DAO → JdbcTemplate → MySQL."
        ),
        (
            "Prompt 2:\n\n"
            "In the same DAO class, implement a separate raw JDBC demonstration method using Class.forName('com.mysql.cj.jdbc.Driver'), DriverManager.getConnection(), and PreparedStatement with proper try-with-resources and exception handling for ClassNotFoundException and SQLException. Explain the step-by-step low-level JDBC workflow and compare it against Spring's JdbcTemplate.",
            
            "Reflection 2:\n\n"
            "This prompt demonstrated the low-level mechanics of Java database connectivity: explicitly loading the JDBC driver bytecode into the JVM via Class.forName(), opening a physical socket connection via DriverManager, preparing a pre-compiled SQL statement with index-based parameter binding, and iterating over the ResultSet cursor. This gave me a deep understanding of why Spring's JdbcTemplate is superior in production: it handles connection pooling, prevents resource leaks, and translates vendor-specific checked SQLExceptions into Spring's unchecked DataAccessException hierarchy."
        ),
        (
            "Prompt 3:\n\n"
            "Add Spring AOP logging to the project using @Aspect, @Component, @Before, @After, and @AfterReturning advice targeting controller and service methods. Also demonstrate @PostConstruct and @PreDestroy lifecycle callbacks in the Service class, and configure a custom prototype-scoped bean (@Scope('prototype')) in @Configuration to record audit actions with dynamic timestamps.",
            
            "Reflection 3:\n\n"
            "Through this prompt, I implemented cross-cutting concerns cleanly using Aspect-Oriented Programming (AOP) without modifying business logic. I learned how AspectJ pointcut expressions intercept execution join points across packages. Furthermore, I understood Spring Bean lifecycles: @PostConstruct fires after dependency injection completes, and @PreDestroy fires before container shutdown. The @Scope('prototype') bean demonstrated that Spring creates a distinct instance upon each request, in contrast to the default Singleton scope."
        )
    ]

    t_prompts = doc.add_table(rows=3, cols=2)
    t_prompts.style = 'Table Grid'
    t_prompts.alignment = WD_TABLE_ALIGNMENT.CENTER
    t_prompts.autofit = False

    for i, (p_text, r_text) in enumerate(prompts_data):
        cp = t_prompts.cell(i, 0)
        cr = t_prompts.cell(i, 1)
        cp.width = Inches(3.3)
        cr.width = Inches(3.4)
        set_cell_margins(cp, 100, 100, 120, 120)
        set_cell_margins(cr, 100, 100, 120, 120)

        p_p = cp.paragraphs[0]
        r_p = p_p.add_run(p_text)
        r_p.font.size = Pt(8.5)

        p_r = cr.paragraphs[0]
        r_r = p_r.add_run(r_text)
        r_r.font.size = Pt(8.5)

    doc.add_page_break()

    # ========================================================================
    # QUESTION 2 SECTION (Mirroring Question 1 / Second Case Study format)
    # ========================================================================
    p_q2 = doc.add_paragraph()
    p_q2.paragraph_format.space_before = Pt(12)
    p_q2.paragraph_format.space_after = Pt(4)
    rq2 = p_q2.add_run("Q2.")
    rq2.font.size = Pt(16)
    rq2.font.bold = True
    rq2.font.color.rgb = RGBColor(15, 23, 42)

    p_link_h2 = doc.add_paragraph()
    p_link_h2.paragraph_format.space_after = Pt(4)
    r_lh2 = p_link_h2.add_run("Moodle upload / GitHub Repository Link")
    r_lh2.font.size = Pt(14)
    r_lh2.font.bold = True

    t_link2 = doc.add_table(rows=2, cols=1)
    t_link2.style = 'Table Grid'
    t_link2.alignment = WD_TABLE_ALIGNMENT.CENTER
    t_link2.autofit = False

    cell_l0_2 = t_link2.cell(0, 0)
    cell_l0_2.width = Inches(6.7)
    set_cell_margins(cell_l0_2, 120, 120, 150, 150)
    p_l0_2 = cell_l0_2.paragraphs[0]
    r_l0_2 = p_l0_2.add_run("GitHub Repository: [FILL: Paste your GitHub repository link here, or 'N/A — Project submitted as ZIP file on Moodle LMS']\n"
                          "Moodle Upload: OOPJ_MSE2_Project_LibraryManagement_[YourName].zip")
    r_l0_2.font.size = Pt(9.5)

    cell_l1_2 = t_link2.cell(1, 0)
    cell_l1_2.width = Inches(6.7)
    set_cell_margins(cell_l1_2, 80, 80, 150, 150)
    cell_l1_2.paragraphs[0].add_run("Submission Status: Complete Spring Boot CRUD Project ready for LMS Upload").font.size = Pt(9)

    doc.add_paragraph().paragraph_format.space_after = Pt(8)

    p_code_h2 = doc.add_paragraph()
    p_code_h2.paragraph_format.space_before = Pt(10)
    p_code_h2.paragraph_format.space_after = Pt(4)
    r_ch2 = p_code_h2.add_run("Project code")
    r_ch2.font.size = Pt(14)
    r_ch2.font.bold = True

    # Note referencing code
    p_note = doc.add_paragraph()
    p_note.add_run("// Same Project Implementation: Library Management REST API\n"
                   "// Technology Stack: Spring Boot + Spring JDBC (JdbcTemplate) + Raw JDBC + MySQL + Spring AOP\n"
                   "// (Refer to detailed source code listings in Q1 for complete implementations of Book.java, BookDao.java, BookService.java, BookController.java, AppConfig.java, LoggingAspect.java, schema.sql, and application.properties)")
    p_note.runs[0].font.name = 'Courier New'
    p_note.runs[0].font.size = Pt(9)
    p_note.runs[0].font.italic = True

    add_code_block(doc, 
        "// KEY HIGHLIGHT: BookController REST Endpoints Implementation\n" +
        code_controller
    )

    # Screenshots for Q2
    p_ss_h2 = doc.add_paragraph()
    p_ss_h2.paragraph_format.space_before = Pt(14)
    p_ss_h2.paragraph_format.space_after = Pt(4)
    r_ssh2 = p_ss_h2.add_run("Screenshots of Model, Controller, and Postman")
    r_ssh2.font.size = Pt(14)
    r_ssh2.font.bold = True

    add_screenshot_placeholder(
        doc,
        "Fig 10: Book.java – Model Class (POJO with Fields, Constructors, Getters & Setters)",
        "Instructions: Paste screenshot of Book.java model code."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 11: BookController.java – REST Controller Implementation",
        "Instructions: Paste screenshot of BookController.java code."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 12: Postman – POST /api/books (201 Created – Create Record)",
        "Instructions: Paste screenshot of POST /api/books request and response."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 13: Postman – GET /api/books (200 OK – Retrieve All Records)",
        "Instructions: Paste screenshot of GET /api/books request and response."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 14: Postman – PUT /api/books/{id} (200 OK – Update Record)",
        "Instructions: Paste screenshot of PUT /api/books/{id} request and response."
    )

    add_screenshot_placeholder(
        doc,
        "Fig 15: Postman – DELETE /api/books/{id} (200 OK – Delete Record)",
        "Instructions: Paste screenshot of DELETE /api/books/{id} request and response."
    )

    # Project Structure for Q2
    p_struct_h2 = doc.add_paragraph()
    p_struct_h2.paragraph_format.space_before = Pt(14)
    p_struct_h2.paragraph_format.space_after = Pt(4)
    r_sth2 = p_struct_h2.add_run("Screenshot of Eclipse/Intellij Project Structure.")
    r_sth2.font.size = Pt(14)
    r_sth2.font.bold = True

    add_screenshot_placeholder(
        doc,
        "Fig 16: Eclipse IDE Project Structure – Library Management System",
        "Instructions: Paste screenshot of Eclipse Package Explorer showing project hierarchy."
    )

    # Prompts for Q2
    p_prompts_h2 = doc.add_paragraph()
    p_prompts_h2.paragraph_format.space_before = Pt(14)
    p_prompts_h2.paragraph_format.space_after = Pt(4)
    r_prh2 = p_prompts_h2.add_run("Write three AI Prompts used for creating the Project.")
    r_prh2.font.size = Pt(14)
    r_prh2.font.bold = True

    t_prompts2 = doc.add_table(rows=3, cols=2)
    t_prompts2.style = 'Table Grid'
    t_prompts2.alignment = WD_TABLE_ALIGNMENT.CENTER
    t_prompts2.autofit = False

    for i, (p_text, r_text) in enumerate(prompts_data):
        cp = t_prompts2.cell(i, 0)
        cr = t_prompts2.cell(i, 1)
        cp.width = Inches(3.3)
        cr.width = Inches(3.4)
        set_cell_margins(cp, 100, 100, 120, 120)
        set_cell_margins(cr, 100, 100, 120, 120)

        p_p = cp.paragraphs[0]
        r_p = p_p.add_run(p_text)
        r_p.font.size = Pt(8.5)

        p_r = cr.paragraphs[0]
        r_r = p_r.add_run(r_text)
        r_r.font.size = Pt(8.5)

    # Save documents
    path1 = r'c:\Users\harsh\eclipse-workspace\Shashwat\OOPJ_MSE2_Submission_Library_Management.docx'
    path2 = r'C:\Users\harsh\Downloads\OOPJ_MSE2_Submission_Library_Management.docx'
    doc.save(path1)
    doc.save(path2)
    print(f'Document successfully created:\n1. {path1}\n2. {path2}')

if __name__ == '__main__':
    build_document()
