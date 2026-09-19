\# LedgerForge



LedgerForge is a full-stack personal finance management application designed to help users track, organize, and analyze their financial activity.



\## Overview



LedgerForge, powered by the SpendSmart application, combines a React frontend with a Java/Spring Boot microservices backend.



The application is organized into independent backend services for authentication, expenses, income, budgets, categories, recurring transactions, analytics, notifications, service discovery, and web/API routing.



\## Features



\- User authentication and account management

\- Expense tracking

\- Income management

\- Budget management

\- Category management

\- Recurring transaction management

\- Financial analytics

\- Reports

\- Notifications

\- User profile management

\- Administrative functionality



\## Architecture



The backend follows a microservices-based architecture.



```text

                         LedgerForge

                             |

              +--------------+--------------+

              |                             |

       SpendSmart Frontend           SpendSmart Backend

              |                             |

           React                    Microservices

                                         |

        +------------+------------+------+------------+

        |            |            |                   |

      Auth        Expense       Income            Budget

        |            |            |                   |

     Category     Recurring    Analytics        Notification

        |            |            |                   |

        +------------+------------+-------------------+

                             |

                     Discovery Server

                             |

                        Web Service


