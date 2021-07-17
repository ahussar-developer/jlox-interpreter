package com.craftinginterpreters.lox;

public class Token {
    final TokenType type;
    final String lexeme; //lexeme examples: var, language, =, "lox", ; >>>> token is everything together + extra info shown
    final Object literal;
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line){
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
