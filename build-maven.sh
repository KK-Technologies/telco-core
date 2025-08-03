#!/bin/bash

# Maven Build Script for Ostelco Core
# This script provides common Maven build operations

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  validate    - Validate project structure"
    echo "  clean       - Clean all modules"
    echo "  compile     - Compile all modules"
    echo "  test        - Run tests"
    echo "  package     - Package all modules"
    echo "  install     - Install to local repository"
    echo "  build       - Clean, compile, test, and package"
    echo "  quick       - Clean and compile (skip tests)"
    echo ""
    echo "Options:"
    echo "  -m MODULE   - Build specific module only"
    echo "  -s          - Skip tests"
    echo "  -o          - Offline mode"
    echo "  -t THREADS  - Number of threads (default: 4)"
    echo "  -h          - Show this help"
    echo ""
    echo "Examples:"
    echo "  $0 build                    # Full build"
    echo "  $0 compile -m model         # Compile model module only"
    echo "  $0 package -s               # Package without tests"
    echo "  $0 install -t 8             # Install with 8 threads"
}

# Default values
COMMAND=""
MODULE=""
SKIP_TESTS=""
OFFLINE=""
THREADS="4"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        validate|clean|compile|test|package|install|build|quick)
            COMMAND="$1"
            shift
            ;;
        -m|--module)
            MODULE="$2"
            shift 2
            ;;
        -s|--skip-tests)
            SKIP_TESTS="-DskipTests"
            shift
            ;;
        -o|--offline)
            OFFLINE="-o"
            shift
            ;;
        -t|--threads)
            THREADS="$2"
            shift 2
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Check if command is provided
if [[ -z "$COMMAND" ]]; then
    print_error "No command specified"
    show_usage
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed or not in PATH"
    exit 1
fi

# Check if Java 17 is available
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [[ "$JAVA_VERSION" != "17" ]]; then
    print_warning "Java version is $JAVA_VERSION, but Java 17 is recommended"
fi

# Build Maven command
MVN_CMD="mvn"

# Add offline flag if specified
if [[ -n "$OFFLINE" ]]; then
    MVN_CMD="$MVN_CMD $OFFLINE"
fi

# Add threading
MVN_CMD="$MVN_CMD -T $THREADS"

# Add module selection if specified
if [[ -n "$MODULE" ]]; then
    MVN_CMD="$MVN_CMD -pl $MODULE"
fi

# Add skip tests if specified
if [[ -n "$SKIP_TESTS" ]]; then
    MVN_CMD="$MVN_CMD $SKIP_TESTS"
fi

# Execute command
case $COMMAND in
    validate)
        print_status "Validating project structure..."
        $MVN_CMD validate
        ;;
    clean)
        print_status "Cleaning project..."
        $MVN_CMD clean
        ;;
    compile)
        print_status "Compiling project..."
        $MVN_CMD compile
        ;;
    test)
        print_status "Running tests..."
        $MVN_CMD test
        ;;
    package)
        print_status "Packaging project..."
        $MVN_CMD package
        ;;
    install)
        print_status "Installing to local repository..."
        $MVN_CMD install
        ;;
    build)
        print_status "Full build (clean, compile, test, package)..."
        $MVN_CMD clean package
        ;;
    quick)
        print_status "Quick build (clean, compile, skip tests)..."
        $MVN_CMD clean compile -DskipTests
        ;;
esac

if [[ $? -eq 0 ]]; then
    print_status "Build completed successfully!"
else
    print_error "Build failed!"
    exit 1
fi