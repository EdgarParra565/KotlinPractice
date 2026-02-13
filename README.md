# KotlinPractice

A collection of Kotlin projects developed while learning functional programming concepts and interactive console applications.

## Projects

### 1. Question Time (Flashcard Study App)
An interactive command-line flashcard application that helps users study different topics through question-answer pairs.

**Features:**
- Multiple question bank support (Geography, Capitals, Perfect Cubes)
- Read questions from text files
- Self-assessment tracking
- Interactive menu system for selecting study topics
- Auto-generated question banks (e.g., perfect cubes)

**Key Concepts Demonstrated:**
- Data classes (`Question`, `QuestionBank`, `State`)
- Enums for state management
- File I/O operations
- React Console pattern for interactive CLI
- Higher-order functions
- Functional programming patterns

### 2. Advanced Question Bank System
An enhanced flashcard system with tagging, machine learning-based answer validation, and multiple question bank types.

**Features:**
- Tagged questions with category filtering
- Multiple question bank implementations:
    - List-based question banks
    - Auto-generated question banks
- ML-powered answer classification using k-Nearest Neighbors
- Levenshtein distance for fuzzy string matching
- Smart answer validation (recognizes variations like "yes", "yep", "yeah")
- Interactive menu system with quit functionality
- Progress tracking (questions attempted, correct answers)

**Key Concepts Demonstrated:**
- Interfaces and polymorphism (`IQuestionBank`, `IMenuOption`, `TaggedQ`)
- Machine learning algorithms (k-NN classifier)
- String similarity algorithms (Levenshtein distance)
- Generic programming (`NamedMenuOption<T>`)
- Type aliases for function types
- State machine pattern
- Comprehensive unit testing

## Technical Skills

- **Functional Programming**: Higher-order functions, map/filter/fold operations
- **Object-Oriented Design**: Interfaces, data classes, inheritance
- **Algorithms**: Levenshtein distance, k-Nearest Neighbors, top-K selection
- **File I/O**: Reading and parsing structured text files
- **State Management**: Enum-based state machines, immutable state updates
- **Testing**: Unit testing with Khoury testing framework
- **User Interaction**: React Console pattern for CLI applications

## How to Run
```bash
# Run Question Time
kotlin -cp khoury.jar question-time.main.kts

# Run Advanced Question Bank
kotlin -cp khoury.jar [filename].main.kts
```

## Dependencies

- Khoury library (for console I/O and testing)

## Testing

Both projects include comprehensive test suites. Tests can be run using:
```bash
# Tests run automatically when executing the scripts
```

## Learning Outcomes

Through these projects, I've gained experience with:
- Designing interactive console applications
- Implementing machine learning algorithms from scratch
- Working with complex data structures
- Writing maintainable, testable code
- Applying functional programming paradigms in Kotlin