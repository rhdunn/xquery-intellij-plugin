/*
 * Copyright (C) 2016-2018 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.xpath.tests.codeInspection

import com.intellij.codeInspection.ProblemHighlightType
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.core.tests.codeInspection.InspectionTestCase
import uk.co.reecedunn.intellij.plugin.intellij.lang.Specification
import uk.co.reecedunn.intellij.plugin.xpath.codeInspection.ijvs.IJVS0001
import uk.co.reecedunn.intellij.plugin.xpath.codeInspection.ijvs.IJVS0002
import uk.co.reecedunn.intellij.plugin.xpath.codeInspection.ijvs.IJVS0003
import uk.co.reecedunn.intellij.plugin.xpath.codeInspection.ijvs.IJVS0004
import uk.co.reecedunn.intellij.plugin.xpath.codeInspection.ijvs.IJVS0005
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

// region XML Entities

private val XML_ENTITIES = listOf("\"",
    "&lt;",
    "&gt;",
    "&amp;",
    "&quot;",
    "&apos;",
    "\"").joinToString()

// endregion
// region HTML4 Entities

private val HTML4_ENTITIES = listOf("\"",
    "&Aacute;",
    "&aacute;",
    "&Acirc;",
    "&acirc;",
    "&acute;",
    "&AElig;",
    "&aelig;",
    "&Agrave;",
    "&agrave;",
    "&alefsym;",
    "&Alpha;",
    "&alpha;",
    "&and;",
    "&ang;",
    "&Aring;",
    "&aring;",
    "&asymp;",
    "&Atilde;",
    "&atilde;",
    "&Auml;",
    "&auml;",
    "&bdquo;",
    "&Beta;",
    "&beta;",
    "&brvbar;",
    "&bull;",
    "&cap;",
    "&Ccedil;",
    "&ccedil;",
    "&cedil;",
    "&cent;",
    "&Chi;",
    "&chi;",
    "&circ;",
    "&clubs;",
    "&cong;",
    "&copy;",
    "&crarr;",
    "&cup;",
    "&curren;",
    "&dagger;",
    "&Dagger;",
    "&darr;",
    "&dArr;",
    "&deg;",
    "&Delta;",
    "&delta;",
    "&diams;",
    "&divide;",
    "&Eacute;",
    "&eacute;",
    "&Ecirc;",
    "&ecirc;",
    "&Egrave;",
    "&egrave;",
    "&empty;",
    "&emsp;",
    "&ensp;",
    "&Epsilon;",
    "&epsilon;",
    "&equiv;",
    "&Eta;",
    "&eta;",
    "&ETH;",
    "&eth;",
    "&Euml;",
    "&euml;",
    "&euro;",
    "&exist;",
    "&fnof;",
    "&forall;",
    "&frac12;",
    "&frac14;",
    "&frac34;",
    "&frasl;",
    "&Gamma;",
    "&gamma;",
    "&ge;",
    "&harr;",
    "&hArr;",
    "&hearts;",
    "&hellip;",
    "&Iacute;",
    "&iacute;",
    "&Icirc;",
    "&icirc;",
    "&iexcl;",
    "&Igrave;",
    "&igrave;",
    "&image;",
    "&infin;",
    "&int;",
    "&Iota;",
    "&iota;",
    "&iquest;",
    "&isin;",
    "&Iuml;",
    "&iuml;",
    "&Kappa;",
    "&kappa;",
    "&Lambda;",
    "&lambda;",
    "&lang;",
    "&laquo;",
    "&larr;",
    "&lArr;",
    "&lceil;",
    "&ldquo;",
    "&le;",
    "&lfloor;",
    "&lowast;",
    "&loz;",
    "&lrm;",
    "&lsaquo;",
    "&lsquo;",
    "&macr;",
    "&mdash;",
    "&micro;",
    "&middot;",
    "&minus;",
    "&Mu;",
    "&mu;",
    "&nabla;",
    "&nbsp;",
    "&ndash;",
    "&ne;",
    "&ni;",
    "&not;",
    "&notin;",
    "&nsub;",
    "&Ntilde;",
    "&ntilde;",
    "&Nu;",
    "&nu;",
    "&Oacute;",
    "&oacute;",
    "&Ocirc;",
    "&ocirc;",
    "&OElig;",
    "&oelig;",
    "&Ograve;",
    "&ograve;",
    "&oline;",
    "&Omega;",
    "&omega;",
    "&Omicron;",
    "&omicron;",
    "&oplus;",
    "&or;",
    "&ordf;",
    "&ordm;",
    "&Oslash;",
    "&oslash;",
    "&Otilde;",
    "&otilde;",
    "&otimes;",
    "&Ouml;",
    "&ouml;",
    "&para;",
    "&part;",
    "&permil;",
    "&perp;",
    "&Phi;",
    "&phi;",
    "&Pi;",
    "&pi;",
    "&piv;",
    "&plusmn;",
    "&pound;",
    "&prime;",
    "&Prime;",
    "&prod;",
    "&prop;",
    "&Psi;",
    "&psi;",
    "&radic;",
    "&rang;",
    "&raquo;",
    "&rarr;",
    "&rArr;",
    "&rceil;",
    "&rdquo;",
    "&real;",
    "&reg;",
    "&rfloor;",
    "&Rho;",
    "&rho;",
    "&rlm;",
    "&rsaquo;",
    "&rsquo;",
    "&sbquo;",
    "&Scaron;",
    "&scaron;",
    "&sdot;",
    "&sect;",
    "&shy;",
    "&Sigma;",
    "&sigma;",
    "&sigmaf;",
    "&sim;",
    "&spades;",
    "&sub;",
    "&sube;",
    "&sum;",
    "&sup;",
    "&sup1;",
    "&sup2;",
    "&sup3;",
    "&supe;",
    "&szlig;",
    "&Tau;",
    "&tau;",
    "&there4;",
    "&Theta;",
    "&theta;",
    "&thetasym;",
    "&thinsp;",
    "&THORN;",
    "&thorn;",
    "&tilde;",
    "&times;",
    "&trade;",
    "&Uacute;",
    "&uacute;",
    "&uarr;",
    "&uArr;",
    "&Ucirc;",
    "&ucirc;",
    "&Ugrave;",
    "&ugrave;",
    "&uml;",
    "&upsih;",
    "&Upsilon;",
    "&upsilon;",
    "&Uuml;",
    "&uuml;",
    "&weierp;",
    "&Xi;",
    "&xi;",
    "&Yacute;",
    "&yacute;",
    "&yen;",
    "&yuml;",
    "&Yuml;",
    "&Zeta;",
    "&zeta;",
    "&zwj;",
    "&zwnj;",
    "\"").joinToString()

// endregion
// region HTML5 Entities

private val HTML5_ENTITIES = listOf("\"",
    "&Abreve;",
    "&abreve;",
    "&ac;",
    "&acd;",
    "&acE;",
    "&Acy;",
    "&acy;",
    "&af;",
    "&Afr;",
    "&afr;",
    "&aleph;",
    "&Amacr;",
    "&amacr;",
    "&amalg;",
    "&AMP;",
    "&And;",
    "&andand;",
    "&andd;",
    "&andslope;",
    "&andv;",
    "&ange;",
    "&angle;",
    "&angmsd;",
    "&angmsdaa;",
    "&angmsdab;",
    "&angmsdac;",
    "&angmsdad;",
    "&angmsdae;",
    "&angmsdaf;",
    "&angmsdag;",
    "&angmsdah;",
    "&angrt;",
    "&angrtvb;",
    "&angrtvbd;",
    "&angsph;",
    "&angst;",
    "&angzarr;",
    "&Aogon;",
    "&aogon;",
    "&Aopf;",
    "&aopf;",
    "&ap;",
    "&apacir;",
    "&apE;",
    "&ape;",
    "&apid;",
    "&ApplyFunction;",
    "&approx;",
    "&approxeq;",
    "&Ascr;",
    "&ascr;",
    "&Assign;",
    "&ast;",
    "&asympeq;",
    "&awconint;",
    "&awint;",
    "&backcong;",
    "&backepsilon;",
    "&backprime;",
    "&backsim;",
    "&backsimeq;",
    "&Backslash;",
    "&Barv;",
    "&barvee;",
    "&Barwed;",
    "&barwed;",
    "&barwedge;",
    "&bbrk;",
    "&bbrktbrk;",
    "&bcong;",
    "&Bcy;",
    "&bcy;",
    "&becaus;",
    "&Because;",
    "&because;",
    "&bemptyv;",
    "&bepsi;",
    "&bernou;",
    "&Bernoullis;",
    "&beth;",
    "&between;",
    "&Bfr;",
    "&bfr;",
    "&bigcap;",
    "&bigcirc;",
    "&bigcup;",
    "&bigodot;",
    "&bigoplus;",
    "&bigotimes;",
    "&bigsqcup;",
    "&bigstar;",
    "&bigtriangledown;",
    "&bigtriangleup;",
    "&biguplus;",
    "&bigvee;",
    "&bigwedge;",
    "&bkarow;",
    "&blacklozenge;",
    "&blacksquare;",
    "&blacktriangle;",
    "&blacktriangledown;",
    "&blacktriangleleft;",
    "&blacktriangleright;",
    "&blank;",
    "&blk12;",
    "&blk14;",
    "&blk34;",
    "&block;",
    "&bne;",
    "&bnequiv;",
    "&bNot;",
    "&bnot;",
    "&Bopf;",
    "&bopf;",
    "&bot;",
    "&bottom;",
    "&bowtie;",
    "&boxbox;",
    "&boxDL;",
    "&boxDl;",
    "&boxdL;",
    "&boxdl;",
    "&boxDR;",
    "&boxDr;",
    "&boxdR;",
    "&boxdr;",
    "&boxH;",
    "&boxh;",
    "&boxHD;",
    "&boxHd;",
    "&boxhD;",
    "&boxhd;",
    "&boxHU;",
    "&boxHu;",
    "&boxhU;",
    "&boxhu;",
    "&boxminus;",
    "&boxplus;",
    "&boxtimes;",
    "&boxUL;",
    "&boxUl;",
    "&boxuL;",
    "&boxul;",
    "&boxUR;",
    "&boxUr;",
    "&boxuR;",
    "&boxur;",
    "&boxV;",
    "&boxv;",
    "&boxVH;",
    "&boxVh;",
    "&boxvH;",
    "&boxvh;",
    "&boxVL;",
    "&boxVl;",
    "&boxvL;",
    "&boxvl;",
    "&boxVR;",
    "&boxVr;",
    "&boxvR;",
    "&boxvr;",
    "&bprime;",
    "&Breve;",
    "&breve;",
    "&Bscr;",
    "&bscr;",
    "&bsemi;",
    "&bsim;",
    "&bsime;",
    "&bsol;",
    "&bsolb;",
    "&bsolhsub;",
    "&bullet;",
    "&bump;",
    "&bumpE;",
    "&bumpe;",
    "&Bumpeq;",
    "&bumpeq;",
    "&Cacute;",
    "&cacute;",
    "&Cap;",
    "&capand;",
    "&capbrcup;",
    "&capcap;",
    "&capcup;",
    "&capdot;",
    "&CapitalDifferentialD;",
    "&caps;",
    "&caret;",
    "&caron;",
    "&Cayleys;",
    "&ccaps;",
    "&Ccaron;",
    "&ccaron;",
    "&Ccirc;",
    "&ccirc;",
    "&Cconint;",
    "&ccups;",
    "&ccupssm;",
    "&Cdot;",
    "&cdot;",
    "&Cedilla;",
    "&cemptyv;",
    "&CenterDot;",
    "&centerdot;",
    "&Cfr;",
    "&cfr;",
    "&CHcy;",
    "&chcy;",
    "&check;",
    "&checkmark;",
    "&cir;",
    "&circeq;",
    "&circlearrowleft;",
    "&circlearrowright;",
    "&circledast;",
    "&circledcirc;",
    "&circleddash;",
    "&CircleDot;",
    "&circledR;",
    "&circledS;",
    "&CircleMinus;",
    "&CirclePlus;",
    "&CircleTimes;",
    "&cirE;",
    "&cire;",
    "&cirfnint;",
    "&cirmid;",
    "&cirscir;",
    "&ClockwiseContourIntegral;",
    "&CloseCurlyDoubleQuote;",
    "&CloseCurlyQuote;",
    "&clubsuit;",
    "&Colon;",
    "&colon;",
    "&Colone;",
    "&colone;",
    "&coloneq;",
    "&comma;",
    "&commat;",
    "&comp;",
    "&compfn;",
    "&complement;",
    "&complexes;",
    "&congdot;",
    "&Congruent;",
    "&Conint;",
    "&conint;",
    "&ContourIntegral;",
    "&Copf;",
    "&copf;",
    "&coprod;",
    "&Coproduct;",
    "&COPY;",
    "&copysr;",
    "&CounterClockwiseContourIntegral;",
    "&Cross;",
    "&cross;",
    "&Cscr;",
    "&cscr;",
    "&csub;",
    "&csube;",
    "&csup;",
    "&csupe;",
    "&ctdot;",
    "&cudarrl;",
    "&cudarrr;",
    "&cuepr;",
    "&cuesc;",
    "&cularr;",
    "&cularrp;",
    "&Cup;",
    "&cupbrcap;",
    "&CupCap;",
    "&cupcap;",
    "&cupcup;",
    "&cupdot;",
    "&cupor;",
    "&cups;",
    "&curarr;",
    "&curarrm;",
    "&curlyeqprec;",
    "&curlyeqsucc;",
    "&curlyvee;",
    "&curlywedge;",
    "&curvearrowleft;",
    "&curvearrowright;",
    "&cuvee;",
    "&cuwed;",
    "&cwconint;",
    "&cwint;",
    "&cylcty;",
    "&daleth;",
    "&Darr;",
    "&dash;",
    "&Dashv;",
    "&dashv;",
    "&dbkarow;",
    "&dblac;",
    "&Dcaron;",
    "&dcaron;",
    "&Dcy;",
    "&dcy;",
    "&DD;",
    "&dd;",
    "&ddagger;",
    "&ddarr;",
    "&DDotrahd;",
    "&ddotseq;",
    "&Del;",
    "&demptyv;",
    "&dfisht;",
    "&Dfr;",
    "&dfr;",
    "&dHar;",
    "&dharl;",
    "&dharr;",
    "&DiacriticalAcute;",
    "&DiacriticalDot;",
    "&DiacriticalDoubleAcute;",
    "&DiacriticalGrave;",
    "&DiacriticalTilde;",
    "&diam;",
    "&Diamond;",
    "&diamond;",
    "&diamondsuit;",
    "&die;",
    "&DifferentialD;",
    "&digamma;",
    "&disin;",
    "&div;",
    "&divideontimes;",
    "&divonx;",
    "&DJcy;",
    "&djcy;",
    "&dlcorn;",
    "&dlcrop;",
    "&dollar;",
    "&Dopf;",
    "&dopf;",
    "&Dot;",
    "&dot;",
    "&DotDot;",
    "&doteq;",
    "&doteqdot;",
    "&DotEqual;",
    "&dotminus;",
    "&dotplus;",
    "&dotsquare;",
    "&doublebarwedge;",
    "&DoubleContourIntegral;",
    "&DoubleDot;",
    "&DoubleDownArrow;",
    "&DoubleLeftArrow;",
    "&DoubleLeftRightArrow;",
    "&DoubleLeftTee;",
    "&DoubleLongLeftArrow;",
    "&DoubleLongLeftRightArrow;",
    "&DoubleLongRightArrow;",
    "&DoubleRightArrow;",
    "&DoubleRightTee;",
    "&DoubleUpArrow;",
    "&DoubleUpDownArrow;",
    "&DoubleVerticalBar;",
    "&DownArrow;",
    "&Downarrow;",
    "&downarrow;",
    "&DownArrowBar;",
    "&DownArrowUpArrow;",
    "&DownBreve;",
    "&downdownarrows;",
    "&downharpoonleft;",
    "&downharpoonright;",
    "&DownLeftRightVector;",
    "&DownLeftTeeVector;",
    "&DownLeftVector;",
    "&DownLeftVectorBar;",
    "&DownRightTeeVector;",
    "&DownRightVector;",
    "&DownRightVectorBar;",
    "&DownTee;",
    "&DownTeeArrow;",
    "&drbkarow;",
    "&drcorn;",
    "&drcrop;",
    "&Dscr;",
    "&dscr;",
    "&DScy;",
    "&dscy;",
    "&dsol;",
    "&Dstrok;",
    "&dstrok;",
    "&dtdot;",
    "&dtri;",
    "&dtrif;",
    "&duarr;",
    "&duhar;",
    "&dwangle;",
    "&DZcy;",
    "&dzcy;",
    "&dzigrarr;",
    "&easter;",
    "&Ecaron;",
    "&ecaron;",
    "&ecir;",
    "&ecolon;",
    "&Ecy;",
    "&ecy;",
    "&eDDot;",
    "&Edot;",
    "&eDot;",
    "&edot;",
    "&ee;",
    "&efDot;",
    "&Efr;",
    "&efr;",
    "&eg;",
    "&egs;",
    "&egsdot;",
    "&el;",
    "&Element;",
    "&elinters;",
    "&ell;",
    "&els;",
    "&elsdot;",
    "&Emacr;",
    "&emacr;",
    "&emptyset;",
    "&EmptySmallSquare;",
    "&emptyv;",
    "&EmptyVerySmallSquare;",
    "&emsp13;",
    "&emsp14;",
    "&ENG;",
    "&eng;",
    "&Eogon;",
    "&eogon;",
    "&Eopf;",
    "&eopf;",
    "&epar;",
    "&eparsl;",
    "&eplus;",
    "&epsi;",
    "&epsiv;",
    "&eqcirc;",
    "&eqcolon;",
    "&eqsim;",
    "&eqslantgtr;",
    "&eqslantless;",
    "&Equal;",
    "&equals;",
    "&EqualTilde;",
    "&equest;",
    "&Equilibrium;",
    "&equivDD;",
    "&eqvparsl;",
    "&erarr;",
    "&erDot;",
    "&Escr;",
    "&escr;",
    "&esdot;",
    "&Esim;",
    "&esim;",
    "&excl;",
    "&Exists;",
    "&expectation;",
    "&ExponentialE;",
    "&exponentiale;",
    "&fallingdotseq;",
    "&Fcy;",
    "&fcy;",
    "&female;",
    "&ffilig;",
    "&fflig;",
    "&ffllig;",
    "&Ffr;",
    "&ffr;",
    "&filig;",
    "&FilledSmallSquare;",
    "&FilledVerySmallSquare;",
    "&fjlig;",
    "&flat;",
    "&fllig;",
    "&fltns;",
    "&Fopf;",
    "&fopf;",
    "&ForAll;",
    "&fork;",
    "&forkv;",
    "&Fouriertrf;",
    "&fpartint;",
    "&frac13;",
    "&frac15;",
    "&frac16;",
    "&frac18;",
    "&frac23;",
    "&frac25;",
    "&frac35;",
    "&frac38;",
    "&frac45;",
    "&frac56;",
    "&frac58;",
    "&frac78;",
    "&frown;",
    "&Fscr;",
    "&fscr;",
    "&gacute;",
    "&Gammad;",
    "&gammad;",
    "&gap;",
    "&Gbreve;",
    "&gbreve;",
    "&Gcedil;",
    "&Gcirc;",
    "&gcirc;",
    "&Gcy;",
    "&gcy;",
    "&Gdot;",
    "&gdot;",
    "&gE;",
    "&gEl;",
    "&gel;",
    "&geq;",
    "&geqq;",
    "&geqslant;",
    "&ges;",
    "&gescc;",
    "&gesdot;",
    "&gesdoto;",
    "&gesdotol;",
    "&gesl;",
    "&gesles;",
    "&Gfr;",
    "&gfr;",
    "&Gg;",
    "&gg;",
    "&ggg;",
    "&gimel;",
    "&GJcy;",
    "&gjcy;",
    "&gl;",
    "&gla;",
    "&glE;",
    "&glj;",
    "&gnap;",
    "&gnapprox;",
    "&gnE;",
    "&gne;",
    "&gneq;",
    "&gneqq;",
    "&gnsim;",
    "&Gopf;",
    "&gopf;",
    "&grave;",
    "&GreaterEqual;",
    "&GreaterEqualLess;",
    "&GreaterFullEqual;",
    "&GreaterGreater;",
    "&GreaterLess;",
    "&GreaterSlantEqual;",
    "&GreaterTilde;",
    "&Gscr;",
    "&gscr;",
    "&gsim;",
    "&gsime;",
    "&gsiml;",
    "&GT;",
    "&Gt;",
    "&gtcc;",
    "&gtcir;",
    "&gtdot;",
    "&gtlPar;",
    "&gtquest;",
    "&gtrapprox;",
    "&gtrarr;",
    "&gtrdot;",
    "&gtreqless;",
    "&gtreqqless;",
    "&gtrless;",
    "&gtrsim;",
    "&gvertneqq;",
    "&gvnE;",
    "&Hacek;",
    "&hairsp;",
    "&half;",
    "&hamilt;",
    "&HARDcy;",
    "&hardcy;",
    "&harrcir;",
    "&harrw;",
    "&Hat;",
    "&hbar;",
    "&Hcirc;",
    "&hcirc;",
    "&heartsuit;",
    "&hercon;",
    "&Hfr;",
    "&hfr;",
    "&HilbertSpace;",
    "&hksearow;",
    "&hkswarow;",
    "&hoarr;",
    "&homtht;",
    "&hookleftarrow;",
    "&hookrightarrow;",
    "&Hopf;",
    "&hopf;",
    "&horbar;",
    "&HorizontalLine;",
    "&Hscr;",
    "&hscr;",
    "&hslash;",
    "&Hstrok;",
    "&hstrok;",
    "&HumpDownHump;",
    "&HumpEqual;",
    "&hybull;",
    "&hyphen;",
    "&ic;",
    "&Icy;",
    "&icy;",
    "&Idot;",
    "&IEcy;",
    "&iecy;",
    "&iff;",
    "&Ifr;",
    "&ifr;",
    "&ii;",
    "&iiiint;",
    "&iiint;",
    "&iinfin;",
    "&iiota;",
    "&IJlig;",
    "&ijlig;",
    "&Im;",
    "&Imacr;",
    "&imacr;",
    "&ImaginaryI;",
    "&imagline;",
    "&imagpart;",
    "&imath;",
    "&imof;",
    "&imped;",
    "&Implies;",
    "&in;",
    "&incare;",
    "&infintie;",
    "&inodot;",
    "&Int;",
    "&intcal;",
    "&integers;",
    "&Integral;",
    "&intercal;",
    "&Intersection;",
    "&intlarhk;",
    "&intprod;",
    "&InvisibleComma;",
    "&InvisibleTimes;",
    "&IOcy;",
    "&iocy;",
    "&Iogon;",
    "&iogon;",
    "&Iopf;",
    "&iopf;",
    "&iprod;",
    "&Iscr;",
    "&iscr;",
    "&isindot;",
    "&isinE;",
    "&isins;",
    "&isinsv;",
    "&isinv;",
    "&it;",
    "&Itilde;",
    "&itilde;",
    "&Iukcy;",
    "&iukcy;",
    "&Jcirc;",
    "&jcirc;",
    "&Jcy;",
    "&jcy;",
    "&Jfr;",
    "&jfr;",
    "&jmath;",
    "&Jopf;",
    "&jopf;",
    "&Jscr;",
    "&jscr;",
    "&Jsercy;",
    "&jsercy;",
    "&Jukcy;",
    "&jukcy;",
    "&kappav;",
    "&Kcedil;",
    "&kcedil;",
    "&Kcy;",
    "&kcy;",
    "&Kfr;",
    "&kfr;",
    "&kgreen;",
    "&KHcy;",
    "&khcy;",
    "&KJcy;",
    "&kjcy;",
    "&Kopf;",
    "&kopf;",
    "&Kscr;",
    "&kscr;",
    "&lAarr;",
    "&Lacute;",
    "&lacute;",
    "&laemptyv;",
    "&lagran;",
    "&Lang;",
    "&langd;",
    "&langle;",
    "&lap;",
    "&Laplacetrf;",
    "&Larr;",
    "&larrb;",
    "&larrbfs;",
    "&larrfs;",
    "&larrhk;",
    "&larrlp;",
    "&larrpl;",
    "&larrsim;",
    "&larrtl;",
    "&lat;",
    "&lAtail;",
    "&latail;",
    "&late;",
    "&lates;",
    "&lBarr;",
    "&lbarr;",
    "&lbbrk;",
    "&lbrace;",
    "&lbrack;",
    "&lbrke;",
    "&lbrksld;",
    "&lbrkslu;",
    "&Lcaron;",
    "&lcaron;",
    "&Lcedil;",
    "&lcedil;",
    "&lcub;",
    "&Lcy;",
    "&lcy;",
    "&ldca;",
    "&ldquor;",
    "&ldrdhar;",
    "&ldrushar;",
    "&ldsh;",
    "&lE;",
    "&LeftAngleBracket;",
    "&LeftArrow;",
    "&Leftarrow;",
    "&leftarrow;",
    "&LeftArrowBar;",
    "&LeftArrowRightArrow;",
    "&leftarrowtail;",
    "&LeftCeiling;",
    "&LeftDoubleBracket;",
    "&LeftDownTeeVector;",
    "&LeftDownVector;",
    "&LeftDownVectorBar;",
    "&LeftFloor;",
    "&leftharpoondown;",
    "&leftharpoonup;",
    "&leftleftarrows;",
    "&LeftRightArrow;",
    "&Leftrightarrow;",
    "&leftrightarrow;",
    "&leftrightarrows;",
    "&leftrightharpoons;",
    "&leftrightsquigarrow;",
    "&LeftRightVector;",
    "&LeftTee;",
    "&LeftTeeArrow;",
    "&LeftTeeVector;",
    "&leftthreetimes;",
    "&LeftTriangle;",
    "&LeftTriangleBar;",
    "&LeftTriangleEqual;",
    "&LeftUpDownVector;",
    "&LeftUpTeeVector;",
    "&LeftUpVector;",
    "&LeftUpVectorBar;",
    "&LeftVector;",
    "&LeftVectorBar;",
    "&lEg;",
    "&leg;",
    "&leq;",
    "&leqq;",
    "&leqslant;",
    "&les;",
    "&lescc;",
    "&lesdot;",
    "&lesdoto;",
    "&lesdotor;",
    "&lesg;",
    "&lesges;",
    "&lessapprox;",
    "&lessdot;",
    "&lesseqgtr;",
    "&lesseqqgtr;",
    "&LessEqualGreater;",
    "&LessFullEqual;",
    "&LessGreater;",
    "&lessgtr;",
    "&LessLess;",
    "&lesssim;",
    "&LessSlantEqual;",
    "&LessTilde;",
    "&lfisht;",
    "&Lfr;",
    "&lfr;",
    "&lg;",
    "&lgE;",
    "&lHar;",
    "&lhard;",
    "&lharu;",
    "&lharul;",
    "&lhblk;",
    "&LJcy;",
    "&ljcy;",
    "&Ll;",
    "&ll;",
    "&llarr;",
    "&llcorner;",
    "&Lleftarrow;",
    "&llhard;",
    "&lltri;",
    "&Lmidot;",
    "&lmidot;",
    "&lmoust;",
    "&lmoustache;",
    "&lnap;",
    "&lnapprox;",
    "&lnE;",
    "&lne;",
    "&lneq;",
    "&lneqq;",
    "&lnsim;",
    "&loang;",
    "&loarr;",
    "&lobrk;",
    "&LongLeftArrow;",
    "&Longleftarrow;",
    "&longleftarrow;",
    "&LongLeftRightArrow;",
    "&Longleftrightarrow;",
    "&longleftrightarrow;",
    "&longmapsto;",
    "&LongRightArrow;",
    "&Longrightarrow;",
    "&longrightarrow;",
    "&looparrowleft;",
    "&looparrowright;",
    "&lopar;",
    "&Lopf;",
    "&lopf;",
    "&loplus;",
    "&lotimes;",
    "&lowbar;",
    "&LowerLeftArrow;",
    "&LowerRightArrow;",
    "&lozenge;",
    "&lozf;",
    "&lpar;",
    "&lparlt;",
    "&lrarr;",
    "&lrcorner;",
    "&lrhar;",
    "&lrhard;",
    "&lrtri;",
    "&Lscr;",
    "&lscr;",
    "&Lsh;",
    "&lsh;",
    "&lsim;",
    "&lsime;",
    "&lsimg;",
    "&lsqb;",
    "&lsquor;",
    "&Lstrok;",
    "&lstrok;",
    "&LT;",
    "&Lt;",
    "&ltcc;",
    "&ltcir;",
    "&ltdot;",
    "&lthree;",
    "&ltimes;",
    "&ltlarr;",
    "&ltquest;",
    "&ltri;",
    "&ltrie;",
    "&ltrif;",
    "&ltrPar;",
    "&lurdshar;",
    "&luruhar;",
    "&lvertneqq;",
    "&lvnE;",
    "&male;",
    "&malt;",
    "&maltese;",
    "&Map;",
    "&map;",
    "&mapsto;",
    "&mapstodown;",
    "&mapstoleft;",
    "&mapstoup;",
    "&marker;",
    "&mcomma;",
    "&Mcy;",
    "&mcy;",
    "&mDDot;",
    "&measuredangle;",
    "&MediumSpace;",
    "&Mellintrf;",
    "&Mfr;",
    "&mfr;",
    "&mho;",
    "&mid;",
    "&midast;",
    "&midcir;",
    "&minusb;",
    "&minusd;",
    "&minusdu;",
    "&MinusPlus;",
    "&mlcp;",
    "&mldr;",
    "&mnplus;",
    "&models;",
    "&Mopf;",
    "&mopf;",
    "&mp;",
    "&Mscr;",
    "&mscr;",
    "&mstpos;",
    "&multimap;",
    "&mumap;",
    "&Nacute;",
    "&nacute;",
    "&nang;",
    "&nap;",
    "&napE;",
    "&napid;",
    "&napos;",
    "&napprox;",
    "&natur;",
    "&natural;",
    "&naturals;",
    "&nbump;",
    "&nbumpe;",
    "&ncap;",
    "&Ncaron;",
    "&ncaron;",
    "&Ncedil;",
    "&ncedil;",
    "&ncong;",
    "&ncongdot;",
    "&ncup;",
    "&Ncy;",
    "&ncy;",
    "&nearhk;",
    "&neArr;",
    "&nearr;",
    "&nearrow;",
    "&nedot;",
    "&NegativeMediumSpace;",
    "&NegativeThickSpace;",
    "&NegativeThinSpace;",
    "&NegativeVeryThinSpace;",
    "&nequiv;",
    "&nesear;",
    "&nesim;",
    "&NestedGreaterGreater;",
    "&NestedLessLess;",
    "&NewLine;",
    "&nexist;",
    "&nexists;",
    "&Nfr;",
    "&nfr;",
    "&ngE;",
    "&nge;",
    "&ngeq;",
    "&ngeqq;",
    "&ngeqslant;",
    "&nges;",
    "&nGg;",
    "&ngsim;",
    "&nGt;",
    "&ngt;",
    "&ngtr;",
    "&nGtv;",
    "&nhArr;",
    "&nharr;",
    "&nhpar;",
    "&nis;",
    "&nisd;",
    "&niv;",
    "&NJcy;",
    "&njcy;",
    "&nlArr;",
    "&nlarr;",
    "&nldr;",
    "&nlE;",
    "&nle;",
    "&nLeftarrow;",
    "&nleftarrow;",
    "&nLeftrightarrow;",
    "&nleftrightarrow;",
    "&nleq;",
    "&nleqq;",
    "&nleqslant;",
    "&nles;",
    "&nless;",
    "&nLl;",
    "&nlsim;",
    "&nLt;",
    "&nlt;",
    "&nltri;",
    "&nltrie;",
    "&nLtv;",
    "&nmid;",
    "&NoBreak;",
    "&NonBreakingSpace;",
    "&Nopf;",
    "&nopf;",
    "&Not;",
    "&NotCongruent;",
    "&NotCupCap;",
    "&NotDoubleVerticalBar;",
    "&NotElement;",
    "&NotEqual;",
    "&NotEqualTilde;",
    "&NotExists;",
    "&NotGreater;",
    "&NotGreaterEqual;",
    "&NotGreaterFullEqual;",
    "&NotGreaterGreater;",
    "&NotGreaterLess;",
    "&NotGreaterSlantEqual;",
    "&NotGreaterTilde;",
    "&NotHumpDownHump;",
    "&NotHumpEqual;",
    "&notindot;",
    "&notinE;",
    "&notinva;",
    "&notinvb;",
    "&notinvc;",
    "&NotLeftTriangle;",
    "&NotLeftTriangleBar;",
    "&NotLeftTriangleEqual;",
    "&NotLess;",
    "&NotLessEqual;",
    "&NotLessGreater;",
    "&NotLessLess;",
    "&NotLessSlantEqual;",
    "&NotLessTilde;",
    "&NotNestedGreaterGreater;",
    "&NotNestedLessLess;",
    "&notni;",
    "&notniva;",
    "&notnivb;",
    "&notnivc;",
    "&NotPrecedes;",
    "&NotPrecedesEqual;",
    "&NotPrecedesSlantEqual;",
    "&NotReverseElement;",
    "&NotRightTriangle;",
    "&NotRightTriangleBar;",
    "&NotRightTriangleEqual;",
    "&NotSquareSubset;",
    "&NotSquareSubsetEqual;",
    "&NotSquareSuperset;",
    "&NotSquareSupersetEqual;",
    "&NotSubset;",
    "&NotSubsetEqual;",
    "&NotSucceeds;",
    "&NotSucceedsEqual;",
    "&NotSucceedsSlantEqual;",
    "&NotSucceedsTilde;",
    "&NotSuperset;",
    "&NotSupersetEqual;",
    "&NotTilde;",
    "&NotTildeEqual;",
    "&NotTildeFullEqual;",
    "&NotTildeTilde;",
    "&NotVerticalBar;",
    "&npar;",
    "&nparallel;",
    "&nparsl;",
    "&npart;",
    "&npolint;",
    "&npr;",
    "&nprcue;",
    "&npre;",
    "&nprec;",
    "&npreceq;",
    "&nrArr;",
    "&nrarr;",
    "&nrarrc;",
    "&nrarrw;",
    "&nRightarrow;",
    "&nrightarrow;",
    "&nrtri;",
    "&nrtrie;",
    "&nsc;",
    "&nsccue;",
    "&nsce;",
    "&Nscr;",
    "&nscr;",
    "&nshortmid;",
    "&nshortparallel;",
    "&nsim;",
    "&nsime;",
    "&nsimeq;",
    "&nsmid;",
    "&nspar;",
    "&nsqsube;",
    "&nsqsupe;",
    "&nsubE;",
    "&nsube;",
    "&nsubset;",
    "&nsubseteq;",
    "&nsubseteqq;",
    "&nsucc;",
    "&nsucceq;",
    "&nsup;",
    "&nsupE;",
    "&nsupe;",
    "&nsupset;",
    "&nsupseteq;",
    "&nsupseteqq;",
    "&ntgl;",
    "&ntlg;",
    "&ntriangleleft;",
    "&ntrianglelefteq;",
    "&ntriangleright;",
    "&ntrianglerighteq;",
    "&num;",
    "&numero;",
    "&numsp;",
    "&nvap;",
    "&nVDash;",
    "&nVdash;",
    "&nvDash;",
    "&nvdash;",
    "&nvge;",
    "&nvgt;",
    "&nvHarr;",
    "&nvinfin;",
    "&nvlArr;",
    "&nvle;",
    "&nvlt;",
    "&nvltrie;",
    "&nvrArr;",
    "&nvrtrie;",
    "&nvsim;",
    "&nwarhk;",
    "&nwArr;",
    "&nwarr;",
    "&nwarrow;",
    "&nwnear;",
    "&oast;",
    "&ocir;",
    "&Ocy;",
    "&ocy;",
    "&odash;",
    "&Odblac;",
    "&odblac;",
    "&odiv;",
    "&odot;",
    "&odsold;",
    "&ofcir;",
    "&Ofr;",
    "&ofr;",
    "&ogon;",
    "&ogt;",
    "&ohbar;",
    "&ohm;",
    "&oint;",
    "&olarr;",
    "&olcir;",
    "&olcross;",
    "&olt;",
    "&Omacr;",
    "&omacr;",
    "&omid;",
    "&ominus;",
    "&Oopf;",
    "&oopf;",
    "&opar;",
    "&OpenCurlyDoubleQuote;",
    "&OpenCurlyQuote;",
    "&operp;",
    "&Or;",
    "&orarr;",
    "&ord;",
    "&order;",
    "&orderof;",
    "&origof;",
    "&oror;",
    "&orslope;",
    "&orv;",
    "&oS;",
    "&Oscr;",
    "&oscr;",
    "&osol;",
    "&Otimes;",
    "&otimesas;",
    "&ovbar;",
    "&OverBar;",
    "&OverBrace;",
    "&OverBracket;",
    "&OverParenthesis;",
    "&par;",
    "&parallel;",
    "&parsim;",
    "&parsl;",
    "&PartialD;",
    "&Pcy;",
    "&pcy;",
    "&percnt;",
    "&period;",
    "&pertenk;",
    "&Pfr;",
    "&pfr;",
    "&phiv;",
    "&phmmat;",
    "&phone;",
    "&pitchfork;",
    "&planck;",
    "&planckh;",
    "&plankv;",
    "&plus;",
    "&plusacir;",
    "&plusb;",
    "&pluscir;",
    "&plusdo;",
    "&plusdu;",
    "&pluse;",
    "&PlusMinus;",
    "&plussim;",
    "&plustwo;",
    "&pm;",
    "&Poincareplane;",
    "&pointint;",
    "&Popf;",
    "&popf;",
    "&Pr;",
    "&pr;",
    "&prap;",
    "&prcue;",
    "&prE;",
    "&pre;",
    "&prec;",
    "&precapprox;",
    "&preccurlyeq;",
    "&Precedes;",
    "&PrecedesEqual;",
    "&PrecedesSlantEqual;",
    "&PrecedesTilde;",
    "&preceq;",
    "&precnapprox;",
    "&precneqq;",
    "&precnsim;",
    "&precsim;",
    "&primes;",
    "&prnap;",
    "&prnE;",
    "&prnsim;",
    "&Product;",
    "&profalar;",
    "&profline;",
    "&profsurf;",
    "&Proportion;",
    "&Proportional;",
    "&propto;",
    "&prsim;",
    "&prurel;",
    "&Pscr;",
    "&pscr;",
    "&puncsp;",
    "&Qfr;",
    "&qfr;",
    "&qint;",
    "&Qopf;",
    "&qopf;",
    "&qprime;",
    "&Qscr;",
    "&qscr;",
    "&quaternions;",
    "&quatint;",
    "&quest;",
    "&questeq;",
    "&QUOT;",
    "&rAarr;",
    "&race;",
    "&Racute;",
    "&racute;",
    "&raemptyv;",
    "&Rang;",
    "&rangd;",
    "&range;",
    "&rangle;",
    "&Rarr;",
    "&rarrap;",
    "&rarrb;",
    "&rarrbfs;",
    "&rarrc;",
    "&rarrfs;",
    "&rarrhk;",
    "&rarrlp;",
    "&rarrpl;",
    "&rarrsim;",
    "&Rarrtl;",
    "&rarrtl;",
    "&rarrw;",
    "&rAtail;",
    "&ratail;",
    "&ratio;",
    "&rationals;",
    "&RBarr;",
    "&rBarr;",
    "&rbarr;",
    "&rbbrk;",
    "&rbrace;",
    "&rbrack;",
    "&rbrke;",
    "&rbrksld;",
    "&rbrkslu;",
    "&Rcaron;",
    "&rcaron;",
    "&Rcedil;",
    "&rcedil;",
    "&rcub;",
    "&Rcy;",
    "&rcy;",
    "&rdca;",
    "&rdldhar;",
    "&rdquor;",
    "&rdsh;",
    "&Re;",
    "&realine;",
    "&realpart;",
    "&reals;",
    "&rect;",
    "&REG;",
    "&ReverseElement;",
    "&ReverseEquilibrium;",
    "&ReverseUpEquilibrium;",
    "&rfisht;",
    "&Rfr;",
    "&rfr;",
    "&rHar;",
    "&rhard;",
    "&rharu;",
    "&rharul;",
    "&rhov;",
    "&RightAngleBracket;",
    "&RightArrow;",
    "&Rightarrow;",
    "&rightarrow;",
    "&RightArrowBar;",
    "&RightArrowLeftArrow;",
    "&rightarrowtail;",
    "&RightCeiling;",
    "&RightDoubleBracket;",
    "&RightDownTeeVector;",
    "&RightDownVector;",
    "&RightDownVectorBar;",
    "&RightFloor;",
    "&rightharpoondown;",
    "&rightharpoonup;",
    "&rightleftarrows;",
    "&rightleftharpoons;",
    "&rightrightarrows;",
    "&rightsquigarrow;",
    "&RightTee;",
    "&RightTeeArrow;",
    "&RightTeeVector;",
    "&rightthreetimes;",
    "&RightTriangle;",
    "&RightTriangleBar;",
    "&RightTriangleEqual;",
    "&RightUpDownVector;",
    "&RightUpTeeVector;",
    "&RightUpVector;",
    "&RightUpVectorBar;",
    "&RightVector;",
    "&RightVectorBar;",
    "&ring;",
    "&risingdotseq;",
    "&rlarr;",
    "&rlhar;",
    "&rmoust;",
    "&rmoustache;",
    "&rnmid;",
    "&roang;",
    "&roarr;",
    "&robrk;",
    "&ropar;",
    "&Ropf;",
    "&ropf;",
    "&roplus;",
    "&rotimes;",
    "&RoundImplies;",
    "&rpar;",
    "&rpargt;",
    "&rppolint;",
    "&rrarr;",
    "&Rrightarrow;",
    "&Rscr;",
    "&rscr;",
    "&Rsh;",
    "&rsh;",
    "&rsqb;",
    "&rsquor;",
    "&rthree;",
    "&rtimes;",
    "&rtri;",
    "&rtrie;",
    "&rtrif;",
    "&rtriltri;",
    "&RuleDelayed;",
    "&ruluhar;",
    "&rx;",
    "&Sacute;",
    "&sacute;",
    "&Sc;",
    "&sc;",
    "&scap;",
    "&sccue;",
    "&scE;",
    "&sce;",
    "&Scedil;",
    "&scedil;",
    "&Scirc;",
    "&scirc;",
    "&scnap;",
    "&scnE;",
    "&scnsim;",
    "&scpolint;",
    "&scsim;",
    "&Scy;",
    "&scy;",
    "&sdotb;",
    "&sdote;",
    "&searhk;",
    "&seArr;",
    "&searr;",
    "&searrow;",
    "&semi;",
    "&seswar;",
    "&setminus;",
    "&setmn;",
    "&sext;",
    "&Sfr;",
    "&sfr;",
    "&sfrown;",
    "&sharp;",
    "&SHCHcy;",
    "&shchcy;",
    "&SHcy;",
    "&shcy;",
    "&ShortDownArrow;",
    "&ShortLeftArrow;",
    "&shortmid;",
    "&shortparallel;",
    "&ShortRightArrow;",
    "&ShortUpArrow;",
    "&sigmav;",
    "&simdot;",
    "&sime;",
    "&simeq;",
    "&simg;",
    "&simgE;",
    "&siml;",
    "&simlE;",
    "&simne;",
    "&simplus;",
    "&simrarr;",
    "&slarr;",
    "&SmallCircle;",
    "&smallsetminus;",
    "&smashp;",
    "&smeparsl;",
    "&smid;",
    "&smile;",
    "&smt;",
    "&smte;",
    "&smtes;",
    "&SOFTcy;",
    "&softcy;",
    "&sol;",
    "&solb;",
    "&solbar;",
    "&Sopf;",
    "&sopf;",
    "&spadesuit;",
    "&spar;",
    "&sqcap;",
    "&sqcaps;",
    "&sqcup;",
    "&sqcups;",
    "&Sqrt;",
    "&sqsub;",
    "&sqsube;",
    "&sqsubset;",
    "&sqsubseteq;",
    "&sqsup;",
    "&sqsupe;",
    "&sqsupset;",
    "&sqsupseteq;",
    "&squ;",
    "&Square;",
    "&square;",
    "&SquareIntersection;",
    "&SquareSubset;",
    "&SquareSubsetEqual;",
    "&SquareSuperset;",
    "&SquareSupersetEqual;",
    "&SquareUnion;",
    "&squarf;",
    "&squf;",
    "&srarr;",
    "&Sscr;",
    "&sscr;",
    "&ssetmn;",
    "&ssmile;",
    "&sstarf;",
    "&Star;",
    "&star;",
    "&starf;",
    "&straightepsilon;",
    "&straightphi;",
    "&strns;",
    "&Sub;",
    "&subdot;",
    "&subE;",
    "&subedot;",
    "&submult;",
    "&subnE;",
    "&subne;",
    "&subplus;",
    "&subrarr;",
    "&Subset;",
    "&subset;",
    "&subseteq;",
    "&subseteqq;",
    "&SubsetEqual;",
    "&subsetneq;",
    "&subsetneqq;",
    "&subsim;",
    "&subsub;",
    "&subsup;",
    "&succ;",
    "&succapprox;",
    "&succcurlyeq;",
    "&Succeeds;",
    "&SucceedsEqual;",
    "&SucceedsSlantEqual;",
    "&SucceedsTilde;",
    "&succeq;",
    "&succnapprox;",
    "&succneqq;",
    "&succnsim;",
    "&succsim;",
    "&SuchThat;",
    "&Sum;",
    "&sung;",
    "&Sup;",
    "&supdot;",
    "&supdsub;",
    "&supE;",
    "&supedot;",
    "&Superset;",
    "&SupersetEqual;",
    "&suphsol;",
    "&suphsub;",
    "&suplarr;",
    "&supmult;",
    "&supnE;",
    "&supne;",
    "&supplus;",
    "&Supset;",
    "&supset;",
    "&supseteq;",
    "&supseteqq;",
    "&supsetneq;",
    "&supsetneqq;",
    "&supsim;",
    "&supsub;",
    "&supsup;",
    "&swarhk;",
    "&swArr;",
    "&swarr;",
    "&swarrow;",
    "&swnwar;",
    "&Tab;",
    "&target;",
    "&tbrk;",
    "&Tcaron;",
    "&tcaron;",
    "&Tcedil;",
    "&tcedil;",
    "&Tcy;",
    "&tcy;",
    "&tdot;",
    "&telrec;",
    "&Tfr;",
    "&tfr;",
    "&Therefore;",
    "&therefore;",
    "&thetav;",
    "&thickapprox;",
    "&thicksim;",
    "&ThickSpace;",
    "&ThinSpace;",
    "&thkap;",
    "&thksim;",
    "&Tilde;",
    "&TildeEqual;",
    "&TildeFullEqual;",
    "&TildeTilde;",
    "&timesb;",
    "&timesbar;",
    "&timesd;",
    "&tint;",
    "&toea;",
    "&top;",
    "&topbot;",
    "&topcir;",
    "&Topf;",
    "&topf;",
    "&topfork;",
    "&tosa;",
    "&tprime;",
    "&TRADE;",
    "&triangle;",
    "&triangledown;",
    "&triangleleft;",
    "&trianglelefteq;",
    "&triangleq;",
    "&triangleright;",
    "&trianglerighteq;",
    "&tridot;",
    "&trie;",
    "&triminus;",
    "&TripleDot;",
    "&triplus;",
    "&trisb;",
    "&tritime;",
    "&trpezium;",
    "&Tscr;",
    "&tscr;",
    "&TScy;",
    "&tscy;",
    "&TSHcy;",
    "&tshcy;",
    "&Tstrok;",
    "&tstrok;",
    "&twixt;",
    "&twoheadleftarrow;",
    "&twoheadrightarrow;",
    "&Uarr;",
    "&Uarrocir;",
    "&Ubrcy;",
    "&ubrcy;",
    "&Ubreve;",
    "&ubreve;",
    "&Ucy;",
    "&ucy;",
    "&udarr;",
    "&Udblac;",
    "&udblac;",
    "&udhar;",
    "&ufisht;",
    "&Ufr;",
    "&ufr;",
    "&uHar;",
    "&uharl;",
    "&uharr;",
    "&uhblk;",
    "&ulcorn;",
    "&ulcorner;",
    "&ulcrop;",
    "&ultri;",
    "&Umacr;",
    "&umacr;",
    "&UnderBar;",
    "&UnderBrace;",
    "&UnderBracket;",
    "&UnderParenthesis;",
    "&Union;",
    "&UnionPlus;",
    "&Uogon;",
    "&uogon;",
    "&Uopf;",
    "&uopf;",
    "&UpArrow;",
    "&Uparrow;",
    "&uparrow;",
    "&UpArrowBar;",
    "&UpArrowDownArrow;",
    "&UpDownArrow;",
    "&Updownarrow;",
    "&updownarrow;",
    "&UpEquilibrium;",
    "&upharpoonleft;",
    "&upharpoonright;",
    "&uplus;",
    "&UpperLeftArrow;",
    "&UpperRightArrow;",
    "&Upsi;",
    "&upsi;",
    "&UpTee;",
    "&UpTeeArrow;",
    "&upuparrows;",
    "&urcorn;",
    "&urcorner;",
    "&urcrop;",
    "&Uring;",
    "&uring;",
    "&urtri;",
    "&Uscr;",
    "&uscr;",
    "&utdot;",
    "&Utilde;",
    "&utilde;",
    "&utri;",
    "&utrif;",
    "&uuarr;",
    "&uwangle;",
    "&vangrt;",
    "&varepsilon;",
    "&varkappa;",
    "&varnothing;",
    "&varphi;",
    "&varpi;",
    "&varpropto;",
    "&vArr;",
    "&varr;",
    "&varrho;",
    "&varsigma;",
    "&varsubsetneq;",
    "&varsubsetneqq;",
    "&varsupsetneq;",
    "&varsupsetneqq;",
    "&vartheta;",
    "&vartriangleleft;",
    "&vartriangleright;",
    "&Vbar;",
    "&vBar;",
    "&vBarv;",
    "&Vcy;",
    "&vcy;",
    "&VDash;",
    "&Vdash;",
    "&vDash;",
    "&vdash;",
    "&Vdashl;",
    "&Vee;",
    "&vee;",
    "&veebar;",
    "&veeeq;",
    "&vellip;",
    "&Verbar;",
    "&verbar;",
    "&Vert;",
    "&vert;",
    "&VerticalBar;",
    "&VerticalLine;",
    "&VerticalSeparator;",
    "&VerticalTilde;",
    "&VeryThinSpace;",
    "&Vfr;",
    "&vfr;",
    "&vltri;",
    "&vnsub;",
    "&vnsup;",
    "&Vopf;",
    "&vopf;",
    "&vprop;",
    "&vrtri;",
    "&Vscr;",
    "&vscr;",
    "&vsubnE;",
    "&vsubne;",
    "&vsupnE;",
    "&vsupne;",
    "&Vvdash;",
    "&vzigzag;",
    "&Wcirc;",
    "&wcirc;",
    "&wedbar;",
    "&Wedge;",
    "&wedge;",
    "&wedgeq;",
    "&Wfr;",
    "&wfr;",
    "&Wopf;",
    "&wopf;",
    "&wp;",
    "&wr;",
    "&wreath;",
    "&Wscr;",
    "&wscr;",
    "&xcap;",
    "&xcirc;",
    "&xcup;",
    "&xdtri;",
    "&Xfr;",
    "&xfr;",
    "&xhArr;",
    "&xharr;",
    "&xlArr;",
    "&xlarr;",
    "&xmap;",
    "&xnis;",
    "&xodot;",
    "&Xopf;",
    "&xopf;",
    "&xoplus;",
    "&xotime;",
    "&xrArr;",
    "&xrarr;",
    "&Xscr;",
    "&xscr;",
    "&xsqcup;",
    "&xuplus;",
    "&xutri;",
    "&xvee;",
    "&xwedge;",
    "&YAcy;",
    "&yacy;",
    "&Ycirc;",
    "&ycirc;",
    "&Ycy;",
    "&ycy;",
    "&Yfr;",
    "&yfr;",
    "&YIcy;",
    "&yicy;",
    "&Yopf;",
    "&yopf;",
    "&Yscr;",
    "&yscr;",
    "&YUcy;",
    "&yucy;",
    "&Zacute;",
    "&zacute;",
    "&Zcaron;",
    "&zcaron;",
    "&Zcy;",
    "&zcy;",
    "&Zdot;",
    "&zdot;",
    "&zeetrf;",
    "&ZeroWidthSpace;",
    "&Zfr;",
    "&zfr;",
    "&ZHcy;",
    "&zhcy;",
    "&zigrarr;",
    "&Zopf;",
    "&zopf;",
    "&Zscr;",
    "&zscr;",
    "\"").joinToString()

// endregion

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - Error and Warning Conditions")
private class PluginInspectionTest : InspectionTestCase() {
    @Nested
    @DisplayName("XQuery IntelliJ Plugin (D.1) Vendor-Specific Behaviour")
    internal inner class IJVSTest {
        @Nested
        @DisplayName("IJVS0001 - unsupported construct")
        internal inner class IJVS0001Test {
            @Nested
            @DisplayName("XQuery")
            internal inner class XQueryTest {
                @Test
                @DisplayName("XQuery 3.0 VersionDecl in XQuery 1.0")
                fun testXQuery30VersionDeclInXQuery10() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    val file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: XQuery version string '1.0' does not support XQuery 3.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_ENCODING))
                }

                @Test
                @DisplayName("XQuery 3.0 VersionDecl in XQuery 3.0")
                fun testXQuery30VersionDecl() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.versionId
                    val file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }
            }

            @Nested
            @DisplayName("XQuery Update Facility")
            internal inner class UpdateFacilityTest {
                @Test
                @DisplayName("Update Facility 1.0: product conforms to the specification")
                fun testUpdateFacility10_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    settings.implementationVersion = "w3c/spec/v1ed"

                    val file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("Update Facility 1.0: product does not conform to the specification")
                fun testUpdateFacility10_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    settings.implementationVersion = "marklogic/v7.0"

                    val file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: MarkLogic 7.0 does not support XQuery Update Facility 1.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_DELETE))
                }

                @Test
                @DisplayName("Update Facility 3.0: product conforms to the specification")
                fun testUpdateFacility30_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.versionId
                    settings.implementationVersion = "w3c/spec/v1ed"

                    val file = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("Update Facility 3.0: incompatible XQuery version")
                fun testUpdateFacility30_IncompatibleXQueryVersion() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    settings.implementationVersion = "w3c/spec/v1ed"

                    val file = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: XQuery version string '1.0' does not support XQuery Update Facility 3.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_INVOKE))
                }

                @Test
                @DisplayName("Update Facility 3.0: product does not conform to the specification")
                fun testUpdateFacility30_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.versionId
                    settings.implementationVersion = "saxon/EE/v9.5" // Supports Update Facility 1.0, not 3.0

                    val file = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Saxon 9.5 does not support XQuery Update Facility 3.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_INVOKE))
                }

                @Test
                @DisplayName("BaseX: product conforms to the specification")
                fun testUpdateFacilityBaseX_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.versionId
                    settings.implementationVersion = "basex/v8.6"

                    val file = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("BaseX: product does not conform to the specification")
                fun testUpdateFacilityBaseX_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.versionId
                    settings.implementationVersion = "saxon/EE/v9.5" // Supports Update Facility 1.0, not 3.0

                    val file = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Saxon 9.5 does not support XQuery Update Facility 3.0, or BaseX 8.5 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_TRANSFORM))
                }
            }

            @Nested
            @DisplayName("XQuery Scripting Extension")
            internal inner class ScriptingTest {
                @Test
                @DisplayName("Scripting Extension 1.0: product conforms to the specification")
                fun testScripting10_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    settings.implementationVersion = "w3c/spec/v1ed"

                    val file = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("Scripting Extension 1.0: product does not conform to the specification")
                fun testScripting10_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    settings.implementationVersion = "marklogic/v7.0"

                    val file = parseResource("tests/parser/xquery-sx-1.0/BlockExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: MarkLogic 7.0 does not support XQuery Scripting Extension 1.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_BLOCK))
                }
            }

            @Nested
            @DisplayName("XQuery 1.0 Working Draft 02 May 2003")
            internal inner class XQueryWorkingDraftTest {
                @Test
                @DisplayName("supported construct; supported XQuery version")
                fun supportedConstructSupportedXQueryVersion() {
                    settings.XQueryVersion = XQuery.WD_1_0_20030502.versionId
                    settings.implementationVersion = "w3c/vwd"
                    val file = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("unsupported construct; supported XQuery version")
                fun unsupportedConstructUnsupportedXQueryVersion() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    settings.implementationVersion = "w3c/v1ed"
                    val file = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: W3C Recommendation (First Edition) does not support XQuery 1.0 (Working Draft 02 May 2003), or XQuery 0.9-ml, or eXist-db < 4.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_EMPTY))
                }

                @Test
                @DisplayName("MarkLogic, xquery 0.9-ml")
                fun markLogicXQuery09ml() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_0_9.versionId
                    settings.implementationVersion = "marklogic/v6.0"
                    val file = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("MarkLogic, xquery 1.0-ml")
                fun markLogicXQuery10ml() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                    settings.implementationVersion = "marklogic/v6.0"
                    val file = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: XQuery version string '1.0-ml' does not support XQuery 1.0 (Working Draft 02 May 2003), or XQuery 0.9-ml, or eXist-db < 4.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_EMPTY))
                }

                @Test
                @DisplayName("MarkLogic, xquery 1.0")
                fun markLogicXQuery10() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    settings.implementationVersion = "marklogic/v6.0"
                    val file = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: XQuery version string '1.0' does not support XQuery 1.0 (Working Draft 02 May 2003), or XQuery 0.9-ml, or eXist-db < 4.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_EMPTY))
                }
            }

            @Nested
            @DisplayName("XQuery 1.0 Recommendation 23 Jan 2007")
            internal inner class XQueryRecTest {
                @Test
                @DisplayName("supported construct; supported XQuery version")
                fun supportedConstructSupportedXQueryVersion() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    settings.implementationVersion = "w3c/vwd"
                    val file = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: W3C Working Draft does not support XQuery 1.0, or eXist-db 4.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_EMPTY_SEQUENCE))
                }

                @Test
                @DisplayName("unsupported construct; supported XQuery version")
                fun unsupportedConstructUnsupportedXQueryVersion() {
                    settings.XQueryVersion = XQuery.WD_1_0_20030502.versionId
                    settings.implementationVersion = "w3c/v1ed"
                    val file = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("MarkLogic, xquery 0.9-ml")
                fun markLogicXQuery09ml() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_0_9.versionId
                    settings.implementationVersion = "marklogic/v6.0"
                    val file = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: XQuery version string '0.9-ml' does not support XQuery 1.0, or eXist-db 4.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_EMPTY_SEQUENCE))
                }

                @Test
                @DisplayName("MarkLogic, xquery 1.0-ml")
                fun markLogicXQuery10ml() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                    settings.implementationVersion = "marklogic/v6.0"
                    val file = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("MarkLogic, xquery 1.0")
                fun markLogicXQuery10() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    settings.implementationVersion = "marklogic/v6.0"
                    val file = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }
            }

            @Nested
            @DisplayName("BaseX")
            internal inner class BaseXTest {
                @Test
                @DisplayName("product conforms to the specification")
                fun testBaseX_ProductConformsToSpecification() {
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.versionId
                    settings.implementationVersion = "basex/v8.5"

                    val file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("product does not conform to the specification")
                fun testBaseX_ProductDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    settings.implementationVersion = "marklogic/v7.0"

                    val file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(2))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: MarkLogic 7.0 does not support BaseX 7.8 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_UPDATE))

                    assertThat(problems[1].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[1].descriptionTemplate,
                        `is`("XPST0003: MarkLogic 7.0 does not support XQuery Update Facility 1.0 constructs.")
                    )
                    assertThat(problems[1].psiElement.node.elementType, `is`(XQueryTokenType.K_DELETE))
                }
            }

            @Nested
            @DisplayName("MarkLogic 6.0 / 0.9-ml")
            internal inner class MarkLogic60Test {
                @Test
                @DisplayName("0.9-ml")
                fun markLogic09ml() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_0_9.versionId
                    settings.implementationVersion = "marklogic/v9.0"

                    val file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("1.0-ml")
                fun markLogic10ml() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                    settings.implementationVersion = "marklogic/v9.0"

                    val file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("product does not conform to the specification")
                fun productDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_0_9.versionId
                    settings.implementationVersion = "saxon/EE/v9.5"

                    val file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Saxon 9.5 does not support MarkLogic 4.0, or XQuery 0.9-ml constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_BINARY))
                }
            }

            @Nested
            @DisplayName("MarkLogic 7.0")
            internal inner class MarkLogic70Test {
                @Test
                @DisplayName("0.9-ml")
                fun markLogic09ml() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_0_9.versionId
                    settings.implementationVersion = "marklogic/v7.0"

                    val file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: XQuery version string '0.9-ml' does not support MarkLogic 7.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_SCHEMA_ROOT))
                }

                @Test
                @DisplayName("1.0-ml")
                fun markLogic10ml() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                    settings.implementationVersion = "marklogic/v7.0"

                    val file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("product does not conform to the specification")
                fun productDoesNotConformToSpecification() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                    settings.implementationVersion = "saxon/EE/v9.5"

                    val file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Saxon 9.5 does not support MarkLogic 7.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_SCHEMA_ROOT))
                }
            }

            @Nested
            @DisplayName("MarkLogic subset of XQuery 3.0")
            internal inner class MarkLogicXQuery30Subset {
                @Test
                @DisplayName("Supported XQuery 3.0 construct with version '0.9-ml'")
                fun xqueryVersion09ml() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_0_9.versionId
                    settings.implementationVersion = "marklogic/v6.0"

                    val file = parseResource("tests/parser/xquery-3.0/Annotation.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: XQuery version string '0.9-ml' does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.ANNOTATION_INDICATOR))
                }

                @Test
                @DisplayName("Supported XQuery 3.0 construct with version '1.0-ml'")
                fun xqueryVersion10ml() {
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                    settings.implementationVersion = "marklogic/v6.0"

                    val file = parseResource("tests/parser/xquery-3.0/Annotation.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("Supported XQuery 3.0 construct with version '1.0'")
                fun xqueryVersion10() {
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    settings.implementationVersion = "marklogic/v6.0"

                    val file = parseResource("tests/parser/xquery-3.0/Annotation.xq")

                    val problems = inspect(file, IJVS0001())
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: XQuery version string '1.0' does not support XQuery 3.0, or MarkLogic 6.0 constructs.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.ANNOTATION_INDICATOR))
                }
            }
        }

        @Nested
        @DisplayName("IJVS0002 - reserved function name")
        internal inner class IJVS0002Test {
            @Nested
            @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
            internal inner class FunctionCall {
                @Nested
                @DisplayName("MarkLogic 8.0 reserved function names")
                internal inner class MarkLogic80 {
                    @Test
                    @DisplayName("in XQuery 1.0")
                    fun testFunctionCall_MarkLogic80ReservedFunctionName_XQuery10() {
                        settings.implementationVersion = "w3c/spec/v1ed"
                        val file =
                            parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in MarkLogic 7.0")
                    fun testFunctionCall_MarkLogic80ReservedFunctionName_MarkLogic70() {
                        settings.implementationVersion = "marklogic/v7"
                        val file =
                            parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in MarkLogic 8.0")
                    fun testFunctionCall_MarkLogic80ReservedFunctionName_MarkLogic80() {
                        settings.implementationVersion = "marklogic/v8"
                        val file =
                            parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(1))

                        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                        assertThat(
                            problems[0].descriptionTemplate,
                            `is`("XPST0003: Reserved MarkLogic 8.0 keyword used as a function name.")
                        )
                        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_ARRAY_NODE))
                    }
                }

                @Nested
                @DisplayName("XQuery Scripting Extension 1.0 reserved function names")
                internal inner class Scripting10 {
                    @Test
                    @DisplayName("in XQuery 1.0")
                    fun testFunctionCall_Scripting10ReservedFunctionName_XQuery10() {
                        settings.implementationVersion = "saxon/HE/v9.5"
                        val file = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in Scripting Extension 1.0")
                    fun testFunctionCall_Scripting10ReservedFunctionName_W3C() {
                        settings.implementationVersion = "w3c/spec/v1ed"
                        val file = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(1))

                        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                        assertThat(
                            problems[0].descriptionTemplate,
                            `is`("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name.")
                        )
                        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_WHILE))
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
            internal inner class FunctionDecl {
                @Nested
                @DisplayName("MarkLogic 8.0 reserved function names")
                internal inner class MarkLogic80 {
                    @Test
                    @DisplayName("in XQuery 1.0")
                    fun testFunctionDecl_MarkLogic80ReservedFunctionName_XQuery10() {
                        settings.implementationVersion = "w3c/spec/v1ed"
                        val file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in MarkLogic 7.0")
                    fun testFunctionDecl_MarkLogic80ReservedFunctionName_MarkLogic70() {
                        settings.implementationVersion = "marklogic/v7"
                        val file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in MarkLogic 8.0")
                    fun testFunctionDecl_MarkLogic80ReservedFunctionName_MarkLogic80() {
                        settings.implementationVersion = "marklogic/v8"
                        val file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(1))

                        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                        assertThat(
                            problems[0].descriptionTemplate,
                            `is`("XPST0003: Reserved MarkLogic 8.0 keyword used as a function name.")
                        )
                        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_ARRAY_NODE))
                    }
                }

                @Nested
                @DisplayName("XQuery Scripting Extension 1.0 reserved function names")
                internal inner class Scripting10 {
                    @Test
                    @DisplayName("in XQuery 1.0")
                    fun testFunctionDecl_Scripting10ReservedFunctionName_XQuery10() {
                        settings.implementationVersion = "saxon/HE/v9.5"
                        val file = parseResource("tests/psi/xquery-sx-1.0/FunctionDecl_ReservedKeyword_While.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in Scripting Extension 1.0")
                    fun testFunctionDecl_Scripting10ReservedFunctionName_W3C() {
                        settings.implementationVersion = "w3c/spec/v1ed"
                        val file = parseResource("tests/psi/xquery-sx-1.0/FunctionDecl_ReservedKeyword_While.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(1))

                        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                        assertThat(
                            problems[0].descriptionTemplate,
                            `is`("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name.")
                        )
                        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_WHILE))
                    }
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (67) NamedFunctionRef")
            internal inner class NamedFunctionRef {
                @Test
                @DisplayName("XQuery 1.0 reserved function names")
                fun testNamedFunctionRef_XQuery10ReservedFunctionName() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword.xq")

                    val problems = inspect(
                        file,
                        IJVS0002()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Reserved XQuery 1.0 keyword used as a function name.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_IF))
                }

                @Test
                @DisplayName("XQuery 3.0 reserved function names")
                fun testNamedFunctionRef_XQuery30ReservedFunctionName() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword_Function.xq")

                    val problems = inspect(
                        file,
                        IJVS0002()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Reserved XQuery 3.0 keyword used as a function name.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_FUNCTION))
                }

                @Nested
                @DisplayName("XQuery Scripting Extension 1.0 reserved function names")
                internal inner class Scripting10 {
                    @Test
                    @DisplayName("in XQuery 1.0")
                    fun testNamedFunctionRef_Scripting10ReservedFunctionName_XQuery10() {
                        settings.implementationVersion = "saxon/HE/v9.5"
                        val file = parseResource("tests/psi/xquery-sx-1.0/NamedFunctionRef_ReservedKeyword_While.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(0))
                    }

                    @Test
                    @DisplayName("in Scripting Extension 1.0")
                    fun testNamedFunctionRef_Scripting10ReservedFunctionName_W3C() {
                        settings.implementationVersion = "w3c/spec/v1ed"
                        val file = parseResource("tests/psi/xquery-sx-1.0/NamedFunctionRef_ReservedKeyword_While.xq")

                        val problems = inspect(
                            file,
                            IJVS0002()
                        )
                        assertThat(problems, `is`(notNullValue()))
                        assertThat(problems!!.size, `is`(1))

                        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                        assertThat(
                            problems[0].descriptionTemplate,
                            `is`("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name.")
                        )
                        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_WHILE))
                    }
                }
            }
        }

        @Nested
        @DisplayName("IJVS0003 - HTML4 and HTML5 predefined entities")
        internal inner class IJVS0003Test {
            private fun checkSupportedEntities(version: Specification, entities: String) {
                settings.XQueryVersion = version.versionId
                if (version == XQuery.MARKLOGIC_0_9 || version == XQuery.MARKLOGIC_1_0) {
                    settings.implementationVersion = "marklogic/v6"
                } else {
                    settings.implementationVersion = "w3c/spec/v1ed"
                }

                val file = parseText(entities)

                val problems = inspect(file,
                    IJVS0003()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(0))
            }

            private fun checkUnsupportedEntities(version: Specification, entities: String, inspectionCount: Int, startsWith: String, endsWith: String, type: ProblemHighlightType = ProblemHighlightType.GENERIC_ERROR_OR_WARNING) {
                settings.XQueryVersion = version.versionId
                if (version == XQuery.MARKLOGIC_0_9 || version == XQuery.MARKLOGIC_1_0) {
                    settings.implementationVersion = "marklogic/v6"
                } else {
                    settings.implementationVersion = "w3c/spec/v1ed"
                }

                val file = parseText(entities)

                val problems = inspect(file,
                    IJVS0003()
                )
                assertThat(problems, `is`(notNullValue()))
                assertThat(problems!!.size, `is`(inspectionCount))

                for (problem in problems) {
                    assertThat(problems[0].highlightType, `is`(type))
                    assertThat(problem.descriptionTemplate, startsWith(startsWith))
                    assertThat(problem.descriptionTemplate, endsWith(endsWith))
                }
            }

            @Nested
            @DisplayName("XML entities")
            internal inner class XMLEntities {
                @Test
                @DisplayName("xquery version 0.9-ml")
                fun testXMLEntities_XQuery_0_9_ML() {
                    checkSupportedEntities(XQuery.MARKLOGIC_0_9,
                        XML_ENTITIES
                    )
                }

                @Test
                @DisplayName("xquery version 1.0")
                fun testXMLEntities_XQuery_1_0() {
                    checkSupportedEntities(XQuery.REC_1_0_20070123,
                        XML_ENTITIES
                    )
                }

                @Test
                @DisplayName("xquery version 1.0-ml")
                fun testXMLEntities_XQuery_1_0_ML() {
                    checkSupportedEntities(XQuery.MARKLOGIC_1_0,
                        XML_ENTITIES
                    )
                }

                @Test
                @DisplayName("xquery version 3.0")
                fun testXMLEntities_XQuery_3_0() {
                    checkSupportedEntities(XQuery.REC_3_0_20140408,
                        XML_ENTITIES
                    )
                }

                @Test
                @DisplayName("xquery version 3.1")
                fun testXMLEntities_XQuery_3_1() {
                    checkSupportedEntities(XQuery.REC_3_1_20170321,
                        XML_ENTITIES
                    )
                }
            }

            @Nested
            @DisplayName("HTML4 entities")
            internal inner class HTML4Entities {
                @Test
                @DisplayName("xquery version 0.9-ml")
                fun testHTML4Entities_XQuery_0_9_ML() {
                    checkSupportedEntities(XQuery.MARKLOGIC_0_9,
                        HTML4_ENTITIES
                    )
                }

                @Test
                @DisplayName("xquery version 1.0")
                fun testHTML4Entities_XQuery_1_0() {
                    checkUnsupportedEntities(
                        XQuery.REC_1_0_20070123,
                        HTML4_ENTITIES, 248,
                        "XPST0003: HTML4 predefined entity '&", ";' is not allowed in this XQuery version."
                    )
                }

                @Test
                @DisplayName("xquery version 1.0-ml")
                fun testHTML4Entities_XQuery_1_0_ML() {
                    checkSupportedEntities(XQuery.MARKLOGIC_1_0,
                        HTML4_ENTITIES
                    )
                }

                @Test
                @DisplayName("xquery version 3.0")
                fun testHTML4Entities_XQuery_3_0() {
                    checkUnsupportedEntities(
                        XQuery.REC_3_0_20140408,
                        HTML4_ENTITIES, 248,
                        "XPST0003: HTML4 predefined entity '&", ";' is not allowed in this XQuery version."
                    )
                }

                @Test
                @DisplayName("xquery version 3.1")
                fun testHTML4Entities_XQuery_3_1() {
                    checkUnsupportedEntities(
                        XQuery.REC_3_1_20170321,
                        HTML4_ENTITIES, 248,
                        "XPST0003: HTML4 predefined entity '&", ";' is not allowed in this XQuery version."
                    )
                }
            }

            @Nested
            @DisplayName("HTML5 entities")
            internal inner class HTML5Entities {
                @Test
                @DisplayName("xquery version 0.9-ml")
                fun testHTML5Entities_XQuery_0_9_ML() {
                    checkSupportedEntities(XQuery.MARKLOGIC_0_9,
                        HTML5_ENTITIES
                    )
                }

                @Test
                @DisplayName("xquery version 1.0")
                fun testHTML5Entities_XQuery_1_0() {
                    checkUnsupportedEntities(
                        XQuery.REC_1_0_20070123,
                        HTML5_ENTITIES, 1872,
                        "XPST0003: HTML5 predefined entity '&", ";' is not allowed in this XQuery version."
                    )
                }

                @Test
                @DisplayName("xquery version 1.0-ml")
                fun testHTML5Entities_XQuery_1_0_ML() {
                    checkSupportedEntities(XQuery.MARKLOGIC_1_0,
                        HTML5_ENTITIES
                    )
                }

                @Test
                @DisplayName("xquery version 3.0")
                fun testHTML5Entities_XQuery_3_0() {
                    checkUnsupportedEntities(
                        XQuery.REC_3_0_20140408,
                        HTML5_ENTITIES, 1872,
                        "XPST0003: HTML5 predefined entity '&", ";' is not allowed in this XQuery version."
                    )
                }

                @Test
                @DisplayName("xquery version 3.1")
                fun testHTML5Entities_XQuery_3_1() {
                    checkUnsupportedEntities(
                        XQuery.REC_3_1_20170321,
                        HTML5_ENTITIES, 1872,
                        "XPST0003: HTML5 predefined entity '&", ";' is not allowed in this XQuery version."
                    )
                }
            }

            @Nested
            @DisplayName("unknoen entities")
            internal inner class UnknownEntities {
                @Test
                @DisplayName("xquery version 0.9-ml")
                fun testUnknownEntities_XQuery_0_9_ML() {
                    checkUnsupportedEntities(
                        XQuery.MARKLOGIC_0_9, "\"&xyz;&ABC;\"", 2,
                        "XPST0003: Predefined entity '&", ";' is not a known entity name.",
                        ProblemHighlightType.ERROR
                    )
                }

                @Test
                @DisplayName("xquery version 1.0")
                fun testUnknownEntities_XQuery_1_0() {
                    checkUnsupportedEntities(
                        XQuery.REC_1_0_20070123, "\"&xyz;&ABC;\"", 2,
                        "XPST0003: Predefined entity '&", ";' is not a known entity name.",
                        ProblemHighlightType.ERROR
                    )
                }

                @Test
                @DisplayName("xquery version 1.0-ml")
                fun testUnknownEntities_XQuery_1_0_ML() {
                    checkUnsupportedEntities(
                        XQuery.MARKLOGIC_1_0, "\"&xyz;&ABC;\"", 2,
                        "XPST0003: Predefined entity '&", ";' is not a known entity name.",
                        ProblemHighlightType.ERROR
                    )
                }

                @Test
                @DisplayName("xquery version 3.0")
                fun testUnknownEntities_XQuery_3_0() {
                    checkUnsupportedEntities(
                        XQuery.REC_3_0_20140408, "\"&xyz;&ABC;\"", 2,
                        "XPST0003: Predefined entity '&", ";' is not a known entity name.",
                        ProblemHighlightType.ERROR
                    )
                }

                @Test
                @DisplayName("xquery version 3.1")
                fun testUnknownEntities_XQuery_3_1() {
                    checkUnsupportedEntities(
                        XQuery.REC_3_1_20170321, "\"&xyz;&ABC;\"", 2,
                        "XPST0003: Predefined entity '&", ";' is not a known entity name.",
                        ProblemHighlightType.ERROR
                    )
                }
            }
        }

        @Nested
        @DisplayName("IJVS0004 - map separator operator")
        internal inner class IJVS0004Test {
            @Nested
            @DisplayName("XQuery 3.1")
            internal inner class XQuery31 {
                @Test
                @DisplayName("XQuery ':' assignment operator")
                fun testXQuery31_Map_XQuerySeparator() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    settings.XQueryVersion = XQuery.REC_3_1_20170321.versionId
                    val file = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq")

                    val problems = inspect(
                        file,
                        IJVS0004()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("Saxon ':=' assignment operator")
                fun testXQuery31_Map_SaxonSeparator() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    settings.XQueryVersion = XQuery.REC_3_1_20170321.versionId
                    val file = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq")

                    val problems = inspect(
                        file,
                        IJVS0004()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Expected ':' (XQuery 3.1/MarkLogic) or ':=' (Saxon 9.4-9.6).")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.ASSIGN_EQUAL))
                }

                @Test
                @DisplayName("missing assignment operator")
                fun testXQuery31_Map_NoValueAssignmentOperator() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    settings.XQueryVersion = XQuery.REC_3_1_20170321.versionId
                    val file = parseResource("tests/psi/xquery-3.1/MapConstructorEntry_NoValueAssignmentOperator.xq")

                    val problems = inspect(
                        file,
                        IJVS0004()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }
            }

            @Nested
            @DisplayName("Saxon 9.4")
            internal inner class Saxon94 {
                @Test
                @DisplayName("Saxon ':=' assignment operator")
                fun testSaxon94_Map_SaxonSeparator() {
                    settings.implementationVersion = "saxon/EE/v9.5"
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.versionId
                    val file = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq")

                    val problems = inspect(
                        file,
                        IJVS0004()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("XQuery ':' assignment operator")
                fun testSaxon94_Map_XQuerySeparator() {
                    settings.implementationVersion = "saxon/EE/v9.5"
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.versionId
                    val file = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq")

                    val problems = inspect(
                        file,
                        IJVS0004()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Expected ':' (XQuery 3.1/MarkLogic) or ':=' (Saxon 9.4-9.6).")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.QNAME_SEPARATOR))
                }

                @Test
                @DisplayName("missing assignment operator")
                fun testSaxon94_Map_NoValueAssignmentOperator() {
                    settings.implementationVersion = "saxon/EE/v9.5"
                    settings.XQueryVersion = XQuery.REC_3_0_20140408.versionId
                    val file = parseResource("tests/psi/xquery-3.1/MapConstructorEntry_NoValueAssignmentOperator.xq")

                    val problems = inspect(
                        file,
                        IJVS0004()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }
            }

            @Nested
            @DisplayName("MarkLogic 8.0")
            internal inner class MarkLogic80 {
                @Test
                @DisplayName("MarkLogic ':' assignment operator")
                fun testMarkLogic80_ObjectNode_MarkLogicSeparator() {
                    settings.implementationVersion = "marklogic/v8"
                    val file = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry.xq")

                    val problems = inspect(
                        file,
                        IJVS0004()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("Saxon ':=' assignment operator")
                fun testMarkLogic80_ObjectNode_SaxonSeparator() {
                    settings.implementationVersion = "marklogic/v8"
                    val file = parseResource("tests/psi/marklogic-8.0/MapConstructorEntry_SaxonSeparator.xq")

                    val problems = inspect(
                        file,
                        IJVS0004()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: Expected ':' (XQuery 3.1/MarkLogic) or ':=' (Saxon 9.4-9.6).")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.ASSIGN_EQUAL))
                }
            }
        }

        @Nested
        @DisplayName("IJVS0005 - final statement semicolon")
        internal inner class IJVS0005Test {
            @Nested
            @DisplayName("MarkLogic")
            internal inner class MarkLogic {
                @Test
                @DisplayName("single statement; without a final statement semicolon")
                fun testMarkLogic_Single_NoSemicolon() {
                    settings.implementationVersion = "marklogic/v6.0"
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                    val file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")

                    val problems = inspect(
                        file,
                        IJVS0005()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("single statement; with a final statement semicolon")
                fun testMarkLogic_Single_Semicolon() {
                    settings.implementationVersion = "marklogic/v6.0"
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                    val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq")

                    val problems = inspect(
                        file,
                        IJVS0005()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("multiple statements; with a final statement semicolon")
                fun testMarkLogic_Multiple_SemicolonAtEnd() {
                    settings.implementationVersion = "marklogic/v6.0"
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                    val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")

                    val problems = inspect(
                        file,
                        IJVS0005()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("multiple statements; without a final statement semicolon")
                fun testMarkLogic_Multiple_NoSemicolonAtEnd() {
                    settings.implementationVersion = "marklogic/v6.0"
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                    val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq")

                    val problems = inspect(
                        file,
                        IJVS0005()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("with prolog")
                fun testMarkLogic_WithProlog() {
                    settings.implementationVersion = "marklogic/v6.0"
                    settings.XQueryVersion = XQuery.MARKLOGIC_1_0.versionId
                    val file = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq")

                    val problems = inspect(
                        file,
                        IJVS0005()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }
            }

            @Nested
            @DisplayName("XQuery Scripting Extension")
            internal inner class ScriptingExtension {
                @Test
                @DisplayName("single statement; without a final statement semicolon")
                fun testScripting_Single_NoSemicolon() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    val file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")

                    val problems = inspect(
                        file,
                        IJVS0005()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("single statement; with a final statement semicolon")
                fun testScripting_Single_Semicolon() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq")

                    val problems = inspect(
                        file,
                        IJVS0005()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("multiple statements; with a final statement semicolon")
                fun testScripting_Multiple_SemicolonAtEnd() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")

                    val problems = inspect(
                        file,
                        IJVS0005()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }

                @Test
                @DisplayName("multiple statements; without a final statement semicolon")
                fun testScripting_Multiple_NoSemicolonAtEnd() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq")

                    val problems = inspect(
                        file,
                        IJVS0005()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(1))

                    assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
                    assertThat(
                        problems[0].descriptionTemplate,
                        `is`("XPST0003: XQuery Scripting Extension 1.0 requires ';' at the end of each statement.")
                    )
                    assertThat(problems[0].psiElement.node.elementType, `is`(XPathTokenType.INTEGER_LITERAL))
                }

                @Test
                @DisplayName("with prolog")
                fun testScripting_WithProlog() {
                    settings.implementationVersion = "w3c/spec/v1ed"
                    settings.XQueryVersion = XQuery.REC_1_0_20070123.versionId
                    val file = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq")

                    val problems = inspect(
                        file,
                        IJVS0005()
                    )
                    assertThat(problems, `is`(notNullValue()))
                    assertThat(problems!!.size, `is`(0))
                }
            }
        }
    }
}
