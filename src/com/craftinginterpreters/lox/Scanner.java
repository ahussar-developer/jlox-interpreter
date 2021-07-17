package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    Scanner(String source){
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()){
            // We are beginning of next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;

    }

    private void scanToken() {
        char c = advance();
        switch (c){
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')){
                    // it is a comment and we need to consume/ignore the whole line
                    while (peek() != '\n' && !isAtEnd()) advance();
                    // do not call addToken because we do NOT care about comments when compiling/interpreting
                }
                else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                //Ignore whitespace
                break;
            case '\n':
                // ignore whitespace but increment line counter
                line++;
                break;
            case'"': string(); break;

            default:
                if (isDigit(c)){
                    number();
                } else if (isAlpha(c)){
                    identifier();
                }
                else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void identifier() {
        //advance/consume letters until you reach a space
        while (isAlphaNumeric(peek())) advance();

        //pull out the word from surce file
        String text = source.substring(start, current);

        //determine if it is a keyword or a user-defined word
        TokenType type = keywords.get(text);

        //Set type to identifier (i.e var name) if it is not a keyword
        if (type == null) type = IDENTIIER;
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) advance();

        //look for a fractional part
        if (peek() == '.' && isDigit(peekNext())){
            // consume "."
            advance();
            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()){
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()){
            Lox.error(line, "Unterminated string.");
            return;
        }

        // closing "
        advance();

        //trim surronding quotes
        String value = source.substring(start+1, current-1);
        addToken(STRING, value);
    }

    private boolean match(char expected){
        if(isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        // only consume character if its what we are looking for
        current++;
        return true;
    }

    //does NOT consume character. It looks one character ahead.
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current+1);
    }

    private boolean isAlpha(char c){
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        char c = source.charAt(current);
        current++;
        return c;
        //Below runs the same as above
        //return source.charAt(current++);
    }

    //overload
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

}
