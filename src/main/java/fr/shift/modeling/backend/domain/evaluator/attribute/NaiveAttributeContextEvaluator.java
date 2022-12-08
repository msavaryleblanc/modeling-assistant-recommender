package fr.shift.modeling.backend.domain.evaluator.attribute;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import java.util.HashMap;

/**
 * Provides a naive individual score aggregation pattern.
 */
public class NaiveAttributeContextEvaluator extends AttributeContextEvaluator {

    public HashMap<String, Double> getInvolvedScores(AttributeContext attributeContext) {
        HashMap<String, Double> output = new HashMap<>();
        switch (attributeContext){
            case CONTEXT_1:
                output.put("s1", 0.5d);
                output.put("s2", 0.5d);
                break;
            case CONTEXT_2:
                output.put("s1", 0.333d);
                output.put("s2", 0.333d);
                output.put("s3", 0.333d);
                break;
            case CONTEXT_3:
                output.put("s1", 0.333d);
                output.put("s2", 0.333d);
                output.put("s4", 0.333d);
                break;
            case CONTEXT_4:
                output.put("s1", 0.25d);
                output.put("s2", 0.25d);
                output.put("s3", 0.25d);
                output.put("s4", 0.25d);
                break;
        }
        return output;
    }
}